package sh.okx.xe.database.skeleton;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sh.okx.xe.XePlugin;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;

public class SkeletonPlayerBalanceDao implements PlayerBalanceDao {
  private final SkeletonCreditDao dao;
  private final PlayerIdMapper mapper;

  private final Map<String, SkeletonBalance> cache = Collections.synchronizedMap(new HashMap<>());

  private final BukkitTask task;

  public SkeletonPlayerBalanceDao(SkeletonCreditDao dao, PlayerIdMapper mapper, int ticks) {
    this.dao = dao;
    this.mapper = mapper;

    XePlugin pl = JavaPlugin.getPlugin(XePlugin.class);
    this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(pl, this::saveAll, ticks, ticks);
  }

  private void saveAll() {
    Set<String> s = cache.keySet();
    synchronized (cache) {
      s.removeIf(l -> {
        SkeletonBalance b = cache.get(l);
        if (b.getServerBalanceChange() == 0) {
          return true;
        }

        dao.saveCredits(l, b);

        b.setCredits(dao.getCredits(l));
        b.setServerBalanceChange(0);
        return false;
      });
    }
  }

  @Override
  public void init() {

  }

  @Override
  public PlayerBalance getPlayerBalance(@Nullable String world, @NotNull UUID uuid,
      Currency currency) {
    SkeletonBalance skeletonBalance = load(mapper.getId(uuid));

    return new PlayerBalance(world, uuid, currency.getId(), BigDecimal.valueOf(skeletonBalance.getTotalCredits()), false);
  }

  @Override
  public boolean save(@NotNull PlayerBalance balance) {
    SkeletonBalance skeletonBalance = load(mapper.getId(balance.getUuid()));
    skeletonBalance.setServerBalanceChange(balance.getBalance().longValueExact() - skeletonBalance.getCredits());
    return true;
  }

  private SkeletonBalance load(String id) {
    synchronized (cache) {
      return cache.computeIfAbsent(id, a -> new SkeletonBalance(dao.getCredits(a), 0));
    }
  }

  @Override
  public void disable() {
    saveAll();
    dao.disable();
    if (task != null) {
      task.cancel();
    }
  }
}
