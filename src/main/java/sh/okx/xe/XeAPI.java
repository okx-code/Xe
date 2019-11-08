package sh.okx.xe;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;

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
      throw new IllegalStateException("XeAPI already initialised!");
    }

    XeAPI.api = new XeAPI(plugin);
  }

  private XeAPI(XePlugin plugin) {
    this.plugin = plugin;
    this.dao = plugin.getDatabase().getPlayerBalanceDao();
  }

  public void deposit(UUID uuid, double amount) {
    deposit(uuid, amount, CurrencyManager.getInstance().getPrimaryCurrency().getMajor());
  }

  public void deposit(UUID uuid, double amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("amount < 0");
    }
    PlayerBalance bal = dao.getPlayerBalance(null, uuid,
        Objects.requireNonNull(CurrencyManager.getInstance().getCurrency(currency)));
    bal.add(BigDecimal.valueOf(amount));
    dao.save(bal);
  }

  public void withdraw(UUID uuid, double amount) {
    withdraw(uuid, amount, CurrencyManager.getInstance().getPrimaryCurrency().getMajor());
  }

  public void withdraw(UUID uuid, double amount, String currency) {
    if (amount < 0) {
      throw new IllegalArgumentException("amount < 0");
    }
    PlayerBalance bal = dao.getPlayerBalance(null, uuid,
        Objects.requireNonNull(CurrencyManager.getInstance().getCurrency(currency)));
    bal.add(BigDecimal.valueOf(-amount));
    dao.save(bal);
  }

  public double getBalance(UUID uuid) {
    return getBalance(uuid, CurrencyManager.getInstance().getPrimaryCurrency().getMajor());
  }

  public double getBalance(UUID uuid, String currency) {
    PlayerBalance bal = dao.getPlayerBalance(null, uuid,
        Objects.requireNonNull(CurrencyManager.getInstance().getCurrency(currency)));
    return bal.getBalance().doubleValue();
  }
}
