package sh.okx.xe.data.display;

import sh.okx.xe.data.Currency;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class LongDisplayFormatter extends SplitDisplayFormatter {
  private final String fill;

  public LongDisplayFormatter(DecimalFormat df, Currency currency, String fill) {
    super(df, currency);
    this.fill = fill;
  }

  @Override
  public String format(BigInteger major, BigInteger minor) {
    return plural(major, currency.getMajor(), currency.getMajorPlural())
        + fill
        + plural(minor, currency.getMinor(),  currency.getMinorPlural());
  }
}
