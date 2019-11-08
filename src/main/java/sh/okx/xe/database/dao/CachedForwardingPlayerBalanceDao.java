package sh.okx.xe.database.dao;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.okx.xe.XePlugin;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.data.PlayerBalance;

public class CachedForwardingPlayerBalanceDao implements PlayerBalanceDao {
  private PlayerBalanceDao dao;

  private final LoadingCache<PlayerBalanceRequest, PlayerBalance> cache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .build(new CacheLoader<PlayerBalanceRequest, PlayerBalance>() {
        @Override
        public PlayerBalance load(@NotNull PlayerBalanceRequest request) {
          return dao.getPlayerBalance(request.world, request.uuid, request.currency);
        }
      });
  private final Map<PlayerBalanceRequest, PlayerBalance> saveQueue = new ConcurrentHashMap<>();

  public CachedForwardingPlayerBalanceDao(PlayerBalanceDao dao) {
    this.dao = dao;

    XePlugin pl = JavaPlugin.getPlugin(XePlugin.class);
    Bukkit.getScheduler().runTaskTimerAsynchronously(pl, () -> save(100), 200, 200);
  }

  private void save(int limit) {
    Iterator<Map.Entry<PlayerBalanceRequest, PlayerBalance>> it = saveQueue.entrySet().iterator();
    int n = 0;
    while (it.hasNext() && (n < limit || limit < 0)) {
      Map.Entry<PlayerBalanceRequest, PlayerBalance> entry = it.next();
      dao.save(entry.getValue());
      it.remove();

      n++;
    }
  }

  @Override
  public void init() {
    dao.init();
  }

  @Override
  public PlayerBalance getPlayerBalance(@Nullable String world, @NotNull UUID uuid, Currency currency) {
    return cache.getUnchecked(new PlayerBalanceRequest(world, uuid, currency));
  }

  @Override
  public boolean save(@NotNull PlayerBalance balance) {
    PlayerBalanceRequest req = new PlayerBalanceRequest(balance.getWorld(), balance.getUuid(), CurrencyManager.getInstance().getCurrency(balance.getCurrencyId()));
    cache.put(req, balance);
    //dao.save(balance);
    saveQueue.put(req, balance);
    return true;
  }

  @RequiredArgsConstructor
  @EqualsAndHashCode
  private static class PlayerBalanceRequest {
    private final String world;
    private final UUID uuid;
    private final Currency currency;
  }

  @Override
  public void disable() {
    save(-1);
    dao.disable();
  }
}
