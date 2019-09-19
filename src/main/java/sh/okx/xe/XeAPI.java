package sh.okx.xe;

import org.bukkit.OfflinePlayer;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;

import java.math.BigDecimal;
import java.util.Objects;

public class XeAPI {
  private XePlugin plugin;
  private PlayerBalanceDao dao;
  private static XeAPI api;

  public static synchronized XeAPI getInstance() {
    if (api == null) {
      throw new IllegalStateException("XeAPI not ready yet");
    }
    return api;
  }

  public static void initAPI(XePlugin plugin) {
    if (XeAPI.api != null) {
      throw new IllegalStateException("XeAPI already initialised!)");
    }

    XeAPI.api = new XeAPI(plugin);
  }

  private XeAPI(XePlugin plugin) {
    this.plugin = plugin;
    this.dao = plugin.getDB().getPlayerBalanceDao();
  }

  public void deposit(OfflinePlayer player, double amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("amount < 0");
    }
    PlayerBalance bal = dao.getPlayerBalance(null, player.getUniqueId(),
        Objects.requireNonNull(CurrencyManager.getInstance().getCurrency(currency)));
    bal.add(BigDecimal.valueOf(amount));
    dao.save(bal);
  }

  public void withdraw(OfflinePlayer player, double amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("amount < 0");
    }
    PlayerBalance bal = dao.getPlayerBalance(null, player.getUniqueId(),
        Objects.requireNonNull(CurrencyManager.getInstance().getCurrency(currency)));
    bal.add(BigDecimal.valueOf(-amount));
    dao.save(bal);
  }

  public double getBalance(OfflinePlayer player, String currency) {
    PlayerBalance bal = dao.getPlayerBalance(null, player.getUniqueId(),
        Objects.requireNonNull(CurrencyManager.getInstance().getCurrency(currency)));
    return bal.getBalance().doubleValue();
  }
}
