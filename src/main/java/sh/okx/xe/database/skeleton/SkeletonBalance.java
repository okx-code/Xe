package sh.okx.xe.database.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SkeletonBalance {

  /**
   * The amount of credits a player has, as of the last time it was retrieved from the database.
   */
  private long credits;
  /**
   * The amount of credits a player's credit balance has changed since the last save to the database.
   */
  private long serverBalanceChange;

  public long getTotalCredits() {
    return credits + serverBalanceChange;
  }
}
