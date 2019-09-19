package sh.okx.xe.migration.migrations;

import co.aikar.commands.MessageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import sh.okx.xe.XePlugin;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.MessageReceiver;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;
import sh.okx.xe.migration.Migration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CraftconomyH2Migration implements Migration {
  private static final int ID_OFFSET = 3000;

  @SuppressWarnings("SqlResolve")
  public void runMigration(PlayerBalanceDao dao, MessageReceiver msg) {
    File dataFolder = JavaPlugin.getPlugin(XePlugin.class).getDataFolder();
    File plugins = dataFolder.getParentFile();
    File craftconomy3 = new File(plugins, "Craftconomy3");
    if (!craftconomy3.exists()) {
      msg.sendMessage(MessageType.ERROR, "Could not find plugins/Craftconomy3 folder");
      return;
    }
    Optional<YamlConfiguration> optConfig = getCraftconomyConfig(msg, craftconomy3);
    if (!optConfig.isPresent()) {
      return;
    }
    YamlConfiguration config = optConfig.get();
    Optional<String> optPrefix = getPrefix(msg, config);
    if (!optPrefix.isPresent()) {
      return;
    }
    String prefix = optPrefix.get();
    msg.sendMessage(MessageType.INFO, "Attempting to connect to database.");
    try {
      Connection connection = DriverManager.getConnection("jdbc:h2:" + new File(craftconomy3, "database"), "sa", null);

      String pacc = prefix + "account";
      String pbal = prefix + "balance";
      String pcur = prefix + "currency";

      YamlConfiguration currenciesConfig = new YamlConfiguration();
      ConfigurationSection currenciesSection = currenciesConfig.createSection("currencies");
      Map<String, ConfigurationSection> currencies = new HashMap<>();

      ResultSet currencyResults = connection.createStatement().executeQuery(
          "SELECT name, plural, minor, minorPlural, sign FROM " + pcur);
      int it = ID_OFFSET;
      while (currencyResults.next()) {
        int id = ++it;
        String major = currencyResults.getString("name");

        String majorPlural = currencyResults.getString("plural");
        String minor = currencyResults.getString("minor");
        String minorPlural = currencyResults.getString("minorPlural");
        String sign = currencyResults.getString("sign");

        ConfigurationSection section = currenciesSection.createSection("craftconomy_" + major.toLowerCase());
        section.set("major", major);
        section.set("major-plural", majorPlural);
        section.set("minor", minor);
        section.set("minor-plural",  minorPlural);
        section.set("format", "#,###");
        section.set("display", "sign");
        section.set("sign", sign);
        section.set("id", id);
        section.set("default", "0");

        msg.sendMessage(MessageType.INFO, "Loading currency: " + major);
        currencies.put(major.toLowerCase(), section);
      }

      File currenciesFile = new File(dataFolder, "currencies.yml");
      File first = getFirstAvailable(dataFolder, "currencies_backup.yml");
      Files.move(currenciesFile.toPath(), first.toPath());
      msg.sendMessage(MessageType.INFO, "Backed up currency file to " + first.getName());
      currenciesConfig.save(currenciesFile);
      msg.sendMessage(MessageType.INFO, "Successfully generated currencies.yml.");

      ResultSet balanceResults = connection.createStatement().executeQuery(
          "SELECT bank, uuid, infiniteMoney, currency_id, balance, worldName FROM " + pacc
              + " INNER JOIN " + pbal
              + " ON " + pacc + ".id = " + pbal + ".username_id");
      int totalBalances = 0;
      while (balanceResults.next()) {
        if (balanceResults.getBoolean("bank")) {
          continue;
        }
        totalBalances++;

        UUID uuid = UUID.fromString(balanceResults.getString("uuid"));
        boolean infinite = balanceResults.getBoolean("infiniteMoney");
        String currencyName = balanceResults.getString("currency_id");
        double balance = balanceResults.getDouble("balance");
        String world = balanceResults.getString("worldName"); // todo add worlds

        ConfigurationSection currency = currencies.get(currencyName.toLowerCase());
        if (currency == null) {
          msg.sendMessage(MessageType.INFO, "Player balance has invalid currency " + currencyName);
          continue;
        }
        int currencyId = currency.getInt("id");

        Currency tempCurrency = new Currency();
        tempCurrency.setId(currencyId);
        tempCurrency.setDefaultAmount(new BigDecimal(currency.getString("default")));

        PlayerBalance pb = dao.getPlayerBalance(world.equalsIgnoreCase("default") ? null : world, uuid, tempCurrency);
        pb.setBalance(BigDecimal.valueOf(balance));
        pb.setInfinite(infinite);

        dao.save(pb);
      }
      msg.sendMessage(MessageType.INFO, "Loaded " + totalBalances + " balances.");

      msg.sendMessage(MessageType.INFO, "Migration success, restart server to apply changes.");
    } catch (SQLException e) {
      e.printStackTrace();
      msg.sendMessage(MessageType.ERROR, "SQL error while accessing Craftconomy database.");
    } catch (IOException e) {
      e.printStackTrace();
      msg.sendMessage(MessageType.ERROR, "File error while migrating files.");
    }
  }

  private Optional<YamlConfiguration> getCraftconomyConfig(MessageReceiver msg, File craftconomyFolder) {
    File configFile = new File(craftconomyFolder, "config.yml");
    if (!configFile.exists()) {
      msg.sendMessage(MessageType.ERROR, "Could not find plugins/Craftconomy3/config.yml");
      return Optional.empty();
    }
    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    return Optional.of(config);
  }

  private Optional<String> getPrefix(MessageReceiver msg, YamlConfiguration config) {
    String prefix = config.getString("System.Database.Prefix");
    if (prefix == null) {
      msg.sendMessage(MessageType.ERROR, "Could not find System.Database.Prefix option in Craftconomy3 config");
      return Optional.empty();
    } else {
      return Optional.of(prefix);
    }
  }

  private static File getFirstAvailable(File folder, String base) {
    String[] parts = base.split("\\.", 2);
    for (int i = 1; i < 10000; i++) {
      File test = new File(folder, parts[0] + i + "." + parts[1]);
      if (!test.exists()) {
        return test;
      }
    }
    throw new IllegalStateException();
  }
}
