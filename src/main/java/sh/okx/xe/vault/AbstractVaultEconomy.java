package sh.okx.xe.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Vault for some reason thinks it's a good idea to use Strings where possible instead of UUIDs
 * smh
 */
public abstract class AbstractVaultEconomy implements Economy {
  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean hasBankSupport() {
    return false;
  }

  public abstract UUID lookupUUID(@NotNull String player);
  public abstract double getBalance(@Nullable String world, @NotNull UUID uuid);
  public abstract EconomyResponse deposit(@Nullable String world, @NotNull UUID uuid, double amount);
  public abstract boolean hasAccount(@Nullable String world, @NotNull UUID uuid);

  /**
   * Withdraw is functionally the same as deposit but with a negative amount
   */
  public EconomyResponse withdraw(@Nullable String world, @NotNull UUID uuid, double amount) {
    return deposit(world, uuid, -amount);
  }

  public boolean has(@Nullable String world, @NotNull UUID uuid, double amount) {
    return getBalance(world, uuid) >= amount;
  }

  @Override
  public boolean hasAccount(@NotNull String playerName) {
    return hasAccount(null, lookupUUID(playerName));
  }

  @Override
  public boolean hasAccount(@NotNull OfflinePlayer player) {
    return hasAccount(null, player.getUniqueId());
  }

  @Override
  public boolean hasAccount(@NotNull String playerName, String worldName) {
    return hasAccount(worldName, lookupUUID(playerName));
  }

  @Override
  public boolean hasAccount(@NotNull OfflinePlayer player, String worldName) {
    return hasAccount(worldName, player.getUniqueId());
  }

  @Override
  public double getBalance(String playerName) {
    return getBalance(null, lookupUUID(playerName));
  }

  @Override
  public double getBalance(OfflinePlayer player) {
    return getBalance(null, player.getUniqueId());
  }

  @Override
  public double getBalance(String playerName, String world) {
    return getBalance(world, lookupUUID(playerName));
  }

  @Override
  public double getBalance(OfflinePlayer player, String world) {
    return getBalance(world, player.getUniqueId());
  }

  @Override
  public boolean has(String playerName, double amount) {
    return has(null, lookupUUID(playerName), amount);
  }

  @Override
  public boolean has(OfflinePlayer player, double amount) {
    return has(null, player.getUniqueId(), amount);
  }

  @Override
  public boolean has(String playerName, String worldName, double amount) {
    return has(worldName, lookupUUID(playerName), amount);
  }

  @Override
  public boolean has(OfflinePlayer player, String worldName, double amount) {
    return has(worldName, player.getUniqueId(), amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(String playerName, double amount) {
    return withdraw(null, lookupUUID(playerName), amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
    return withdraw(null, player.getUniqueId(), amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
    return withdraw(worldName, lookupUUID(playerName), amount);
  }

  @Override
  public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
    return withdraw(worldName, player.getUniqueId(), amount);
  }

  @Override
  public EconomyResponse depositPlayer(String playerName, double amount) {
    return deposit(null, lookupUUID(playerName), amount);
  }

  @Override
  public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
    return deposit(null, player.getUniqueId(), amount);
  }

  @Override
  public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
    return deposit(worldName, lookupUUID(playerName), amount);
  }

  @Override
  public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
    return deposit(worldName, player.getUniqueId(), amount);
  }

  @Override
  public EconomyResponse createBank(String name, String player) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse createBank(String name, OfflinePlayer player) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse deleteBank(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse bankBalance(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse bankHas(String name, double amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse bankWithdraw(String name, double amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse bankDeposit(String name, double amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse isBankOwner(String name, String playerName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse isBankMember(String name, String playerName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EconomyResponse isBankMember(String name, OfflinePlayer player) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> getBanks() {
    throw new UnsupportedOperationException();
  }

  // don't bother making accounts, just return if one already exists
  @Override
  public boolean createPlayerAccount(String playerName) {
    return !hasAccount(playerName);
  }

  @Override
  public boolean createPlayerAccount(OfflinePlayer player) {
    return !hasAccount(player);
  }

  @Override
  public boolean createPlayerAccount(String playerName, String worldName) {
    return !hasAccount(playerName, worldName);
  }

  @Override
  public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
    return !hasAccount(player, worldName);
  }
}
