package sh.okx.xe.database.skeleton;

import java.util.HashMap;
import java.util.Map;

public class DummySkeletonCreditDao implements SkeletonCreditDao {
  private Map<String, Long> credits = new HashMap<>();

  public DummySkeletonCreditDao() {
    credits.put("115090410849828865", 100L); // okx
    credits.put("356268554494148608", 1000L); // elli
  }

  @Override
  public long getCredits(String userId) {
    System.out.println(userId + " has: " + this.credits.get(userId));
    return this.credits.get(userId);
  }

  @Override
  public void saveCredits(String userId, SkeletonBalance balance) {
    this.credits.put(userId, balance.getTotalCredits());
    System.out.println(userId + " saving: " + balance.getCredits() + " + " + balance.getServerBalanceChange() + " = " + balance.getTotalCredits());
  }

  @Override
  public void disable() {

  }
}
