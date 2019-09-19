package sh.okx.xe.database.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.PlayerBalance;

import java.util.UUID;

public interface PlayerBalanceDao {
  void init();
  PlayerBalance getPlayerBalance(@Nullable String world, @NotNull UUID uuid, Currency currency);
  boolean save(@NotNull PlayerBalance balance);
  void disable();
}
