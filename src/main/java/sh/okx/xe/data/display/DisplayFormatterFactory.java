package sh.okx.xe.data.display;

import org.jetbrains.annotations.NotNull;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.display.formatters.MajorDisplayFormatter;
import sh.okx.xe.data.display.formatters.MinorDisplayFormatter;
import sh.okx.xe.data.display.formatters.SignDisplayFormatter;
import sh.okx.xe.data.display.formatters.SignFrontDisplayFormatter;

import java.text.DecimalFormat;

public class DisplayFormatterFactory {
  @NotNull
  public static DisplayFormatter get(@NotNull String display, @NotNull DecimalFormat df, @NotNull Currency currency) {
    switch (display.toLowerCase()) {
      case "sign":
        return new SignDisplayFormatter(df, currency);
      case "signfront":
        return new SignFrontDisplayFormatter(df, currency);
      case "long":
        return new LongDisplayFormatter(df, currency, " ");
      case "longcomma":
        return new LongDisplayFormatter(df, currency, ", ");
      case "longand":
        return new LongDisplayFormatter(df, currency, " and ");
      case "major":
        return new MajorDisplayFormatter(df, currency);
      case "minor":
        return new MinorDisplayFormatter(df, currency);
      default:
        throw new IllegalArgumentException("Could not find display formatter for " + display);
    }
  }
}
