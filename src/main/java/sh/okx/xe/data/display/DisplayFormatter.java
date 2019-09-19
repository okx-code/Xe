package sh.okx.xe.data.display;

import lombok.RequiredArgsConstructor;
import sh.okx.xe.data.Currency;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

@RequiredArgsConstructor
public abstract class DisplayFormatter {
  protected final DecimalFormat df;
  protected final Currency currency;

  public abstract String format(BigDecimal amount);

  protected String plural(BigInteger d, String sng, String prl) {
    return df.format(d) + " " + (d.equals(BigInteger.ONE) ? sng : prl);
  }

  protected String plural(BigDecimal d, String sng, String prl) {
    return df.format(d) + " " + (d.compareTo(BigDecimal.ONE) == 0 ? sng : prl);
  }
}
