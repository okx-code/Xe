package sh.okx.xe;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.MinecraftMessageKeys;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import sh.okx.xe.command.CommandBalance;
import sh.okx.xe.command.CommandEconomy;
import sh.okx.xe.command.CommandPerCurrency;
import sh.okx.xe.command.CommandXe;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.database.Database;
import sh.okx.xe.database.SimpleDatabase;
import sh.okx.xe.database.dao.PlayerBalanceDao;
import sh.okx.xe.database.skeleton.DiscordSrvIdMapper;
import sh.okx.xe.database.skeleton.MongoSkeletonCreditDao;
import sh.okx.xe.database.skeleton.SkeletonPlayerBalanceDao;
import sh.okx.xe.migration.Migration;
import sh.okx.xe.migration.MigrationFactory;
import sh.okx.xe.papi.XeExpansion;
import sh.okx.xe.vault.VaultHookManager;

public class XePlugin extends JavaPlugin {
  @Nullable
  private VaultHookManager vaultHookManager = null;

  private Locale locale;
  private Database database;

  public Database getDatabase() {
    return database;
  }

  @Override
  public void onEnable() {

    saveDefaultConfig();
    save("lang_en.yml");

    locale = Locale.forLanguageTag(getConfig().getString("locale", "en"));

    loadCurrencies();

//    database = new SqliteDatabase(getDataFolder(), getConfig().getBoolean("per-world"));
    database = new SimpleDatabase(new SkeletonPlayerBalanceDao(
        new MongoSkeletonCreditDao(
            getConfig().getString("mongo.url"),
            getConfig().getString("mongo.database"),
            getConfig().getString("mongo.collection")),
        new DiscordSrvIdMapper(),
        getConfig().getInt("cache-ticks")));
    database.init();

    tryVaultHook();
    registerCommands();
    hookPlaceholderApi();

    XeAPI.initAPI(this);
  }

  private void registerCommands() {
    BukkitCommandManager manager = new BukkitCommandManager(this);

    //noinspection deprecation
    manager.enableUnstableAPI("help");

    manager.getCommandCompletions().registerAsyncCompletion("currencies", c -> CurrencyManager.getInstance().getCurrencyNames());

    try {
      manager.getLocales().loadYamlLanguageFile("lang_en.yml", Locale.ENGLISH);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }

    setColours(manager);

    manager.registerDependency(PlayerBalanceDao.class, database.getPlayerBalanceDao());

    CommandContexts<BukkitCommandExecutionContext> contexts = manager.getCommandContexts();
    contexts.registerOptionalContext(Currency.class, CurrencyManager.getInstance().getContextResolver());
    contexts.registerOptionalContext(OfflinePlayer.class, c -> {
      String name = c.popFirstArg();
      if (name == null) {
        CommandSender sender = c.getSender();
        if (sender instanceof Player) {
          return (Player) sender;
        } else {
          throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE);
        }
      }
      Player onlinePlayer = XeUtil.findPlayerExtraSmart(c.getIssuer(), name);
      if (onlinePlayer != null) {
        return onlinePlayer;
      }
      OfflinePlayer offlinePlayer;
      try {
        offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(name));
      } catch (IllegalArgumentException ignored) {
        //noinspection deprecation
        offlinePlayer = Bukkit.getOfflinePlayer(name);
      }
      //noinspection ConstantConditions
      if (offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())) {
        throw new InvalidCommandArgument(MinecraftMessageKeys.NO_PLAYER_FOUND_OFFLINE,
            "{search}", name);
      }
      return offlinePlayer;
    });
    contexts.registerContext(Migration.class, c -> {
      String name = c.popFirstArg();
      Migration migration = MigrationFactory.get(name);
      if (migration == null) {
        throw new InvalidCommandArgument("Migration not found");
      }
      return migration;
    });

    manager.registerCommand(new CommandXe());

    CommandEconomy commandEconomy = new CommandEconomy();
    manager.registerCommand(commandEconomy);

    for (Currency currency : CurrencyManager.getInstance().getCurrencies()) {
      String alias = currency.getAlias();
      String bal = currency.getBalanceCommand();
      if (alias != null) {
        manager.registerCommand(new CommandPerCurrency(commandEconomy, currency, alias));
      }
      if (bal != null) {
        manager.registerCommand(new CommandBalance(commandEconomy, currency, bal));
      }
    }
  }

  private void setColours(BukkitCommandManager manager) {
    ConfigurationSection section = loadConfig("lang_en.yml").getConfigurationSection("colours");
    if (section == null) {
      getLogger().severe("No colours found in locale file");
      return;
    }

    manager.setFormat(MessageType.ERROR, getChatColours(section, "error"));
    manager.setFormat(MessageType.SYNTAX, getChatColours(section, "syntax"));
    manager.setFormat(MessageType.INFO, getChatColours(section, "info"));
    manager.setFormat(MessageType.HELP, getChatColours(section, "help"));
  }

  private ChatColor[] getChatColours(ConfigurationSection section, String path) {
    List<Character> chars = section.getCharacterList(path);
    ChatColor[] colours = new ChatColor[chars.size()];
    for (int i = 0; i < colours.length; i++) {
      // make sure 0-9 gets converted to the actual character
      char code = chars.get(i);
      if (code < 10) {
        code += '0';
      }
      ChatColor byChar = ChatColor.getByChar(code);
      if (byChar == null) {
        getLogger().severe("Chat colour &" + code + " does not exist");
      }
      colours[i] = byChar;
    }
    return colours;
  }

  private void loadCurrencies() {
    ConfigurationSection currencies = loadConfig("currencies.yml").getConfigurationSection("currencies");
    for (String key : currencies.getKeys(false)) {
      ConfigurationSection section = currencies.getConfigurationSection(key);
      if (section != null) {
        Currency currency = Currency.deserialize(section, locale);
        CurrencyManager.getInstance().addCurrency(currency);
        if (currency.getId() == getConfig().getInt("primary-currency-id")) {
          CurrencyManager.getInstance().setPrimaryCurrency(currency);
        }
      }
    }

    if (CurrencyManager.getInstance().getPrimaryCurrency() == null) {
      throw new RuntimeException("No primary currency found!");
    }
  }

  private void tryVaultHook() {
    if (this.vaultHookManager != null) {
      return; // already hooked
    }

    try {
      if (getServer().getPluginManager().isPluginEnabled("Vault")) {
        this.vaultHookManager = new VaultHookManager(this);
        this.vaultHookManager.hook();
        getLogger().info("Registered Vault economy hook.");
      }
    } catch (Exception e) {
      this.vaultHookManager = null;
      getLogger().severe("Error occurred whilst hooking into Vault.");
      e.printStackTrace();
    }
  }

  private void hookPlaceholderApi() {
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new XeExpansion(this).register();
    }
  }

  @Override
  public void onDisable() {
    database.getPlayerBalanceDao().disable();
    if (this.vaultHookManager != null) {
      this.vaultHookManager.unhook();
    }
  }

  public boolean option(String option) {
    return getConfig().getBoolean(option);
  }

  public void save(String name) {
    File file = new File(getDataFolder(), name);
    if (!file.exists()) {
      saveResource(name, false);
    }
  }

  /**
   * this should really be part of spigot
   */
  public YamlConfiguration loadConfig(String name) {
    File file = new File(getDataFolder(), name);
    if (!file.exists()) {
      saveResource(name, false);
    }
    return YamlConfiguration.loadConfiguration(file);
  }
}
