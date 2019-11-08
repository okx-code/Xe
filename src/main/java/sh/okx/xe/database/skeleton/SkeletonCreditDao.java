package sh.okx.xe.database.skeleton;

public interface SkeletonCreditDao {
  long getCredits(String userId);
  void saveCredits(String userId, SkeletonBalance balance);
  void disable();
}
