package sh.okx.xe.vault;

import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.okx.xe.XePlugin;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class XeVaultEconomy extends AbstractVaultEconomy {
  private final Currency primaryCurrency;
  private final PlayerBalanceDao dao;

  public XeVaultEconomy(XePlugin xe) {
    this.dao = xe.getDB().getPlayerBalanceDao();
    this.primaryCurrency = CurrencyManager.getInstance().getPrimaryCurrency();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "XePlugin";
  }

  @Override
  public boolean hasBankSupport() {
    return false;
  }

  @SuppressWarnings("deprecation")
  @Override
  public UUID lookupUUID(@NotNull String player) {
    return Bukkit.getOfflinePlayer(player).getUniqueId();
  }

  @Override
  public boolean hasAccount(@Nullable String world, @NotNull UUID uuid) {
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    return offlinePlayer.isOnline() || offlinePlayer.hasPlayedBefore();
  }

  @Override
  public double getBalance(@Nullable String world, @NotNull UUID uuid) {
    PlayerBalance playerBalance = dao.getPlayerBalance(world, uuid, primaryCurrency);
    if (playerBalance == null) {
      return 0; // could not retrieve balance
    } else if (playerBalance.isInfinite()) {
      return Double.POSITIVE_INFINITY;
    }

    return playerBalance.getBalance().doubleValue();
  }

  @Override
  public boolean has(@Nullable String world, @NotNull UUID uuid, double amount) {
    PlayerBalance playerBalance = dao.getPlayerBalance(world, uuid, primaryCurrency);
    if (playerBalance.isInfinite()) {
      return true;
    } else {
      return playerBalance.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
    }
  }

  @Override
  public EconomyResponse deposit(@Nullable String world, @NotNull UUID uuid, double amount) {
    PlayerBalance playerBalance = dao.getPlayerBalance(world, uuid, primaryCurrency);
    if (playerBalance == null) {
      return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "Could not retrieve balance");
    } else if (playerBalance.isInfinite()) {
      return new EconomyResponse(amount, Double.POSITIVE_INFINITY, EconomyResponse.ResponseType.SUCCESS, null);
    }
    BigDecimal balance = playerBalance.getBalance().add(BigDecimal.valueOf(amount));
    playerBalance.setBalance(balance);
    double newBalance = balance.doubleValue();
    boolean save = dao.save(playerBalance);
    if (save) {
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    } else {
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.FAILURE, "Failed to save");
    }
  }

  @Override
  public int fractionalDigits() {
    return 9;
  }

  @Override
  public String format(double amount) {
    return primaryCurrency.getFormatter().format(BigDecimal.valueOf(amount));
  }

  @Override
  public String currencyNamePlural() {
    return primaryCurrency.getMajorPlural();
  }

  @Override
  public String currencyNameSingular() {
    return primaryCurrency.getMajor();
  }
}
