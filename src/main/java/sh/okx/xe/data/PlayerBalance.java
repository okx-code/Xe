package sh.okx.xe.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerBalance {
  private String world;
  private final UUID uuid;
  private final int currencyId;
  @Setter
  private BigDecimal balance;
  private boolean infinite;

  public void add(BigDecimal decimal) {
    balance = balance.add(decimal);
  }

  public String getBalancedFormatted() {
    return CurrencyManager.getInstance().getCurrency(currencyId).getFormatter().format(balance);
  }
}
