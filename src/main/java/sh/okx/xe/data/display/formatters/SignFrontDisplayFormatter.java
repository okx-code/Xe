package sh.okx.xe.data.display.formatters;

import sh.okx.xe.data.Currency;
import sh.okx.xe.data.display.DisplayFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class SignFrontDisplayFormatter extends DisplayFormatter {
  public SignFrontDisplayFormatter(DecimalFormat df, Currency currency) {
    super(df, currency);
  }

  @Override
  public String format(BigDecimal amount) {
    return df.format(amount) + currency.getSign();
  }
}
