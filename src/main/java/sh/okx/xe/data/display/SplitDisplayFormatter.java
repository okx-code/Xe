package sh.okx.xe.data.display;

import sh.okx.xe.data.Currency;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class SplitDisplayFormatter extends DisplayFormatter {
  public SplitDisplayFormatter(DecimalFormat df, Currency currency) {
    super(df, currency);
  }

  @Override
  public String format(BigDecimal amount) {
    BigDecimal floored = amount.round(new MathContext(34, RoundingMode.FLOOR));
    BigInteger major = floored.toBigIntegerExact();
    BigInteger minor = amount.subtract(floored).scaleByPowerOfTen(2).round(MathContext.DECIMAL128).toBigIntegerExact();
    return format(major, minor);
  }

  public abstract String format(BigInteger major, BigInteger minor);
}
