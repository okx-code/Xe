package sh.okx.xe.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import sh.okx.xe.data.display.DisplayFormatter;
import sh.okx.xe.data.display.DisplayFormatterFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Data
@NoArgsConstructor
public class Currency {
  private String major;
  private String majorPlural;
  private String minor;
  private String minorPlural;
  private String sign = "$";
  private DisplayFormatter formatter;
  private int id;
  private BigDecimal defaultAmount = BigDecimal.ZERO;
  private String alias;
  private String balanceCommand;

  @SuppressWarnings("ConstantConditions")
  public static Currency deserialize(ConfigurationSection section, Locale locale) {
    Currency currency = new Currency();
    currency.major = section.getString("major", currency.major);
    currency.majorPlural = section.getString("major-plural", currency.majorPlural);
    currency.minor = section.getString("minor", currency.minor);
    currency.minorPlural = section.getString("minor-plural", currency.minorPlural);
    currency.sign = section.getString("sign", currency.sign);
    currency.id = section.getInt("id");
    if (currency.id == 0) {
      throw new IllegalArgumentException("Currency ID cannot be 0 for currency " + section.getName());
    }
    if (section.contains("default")) {
      currency.defaultAmount = new BigDecimal(section.getString("default"), MathContext.DECIMAL128);
    }
    currency.alias = section.getString("alias", currency.alias);
    currency.balanceCommand = section.getString("balance", currency.balanceCommand);

    String format = section.getString("format", "#,###");
    DecimalFormat df = new DecimalFormat(
        format,
        DecimalFormatSymbols.getInstance(locale));
    String display = section.getString("display", "sign");
    currency.formatter = DisplayFormatterFactory.get(display, df, currency);

    return currency;
  }

  public String[] replace(String... args) {
    String[] array = {
        "{major}", major,
        "{minor}", minor,
        "{majorplural}", majorPlural,
        "{minorplural}", minorPlural,
    };

    String[] alloc = new String[args.length + array.length];
    System.arraycopy(args, 0, alloc, 0, args.length);
    System.arraycopy(array, 0, alloc, args.length, array.length);
    return alloc;
  }

  public boolean equals(Object o) {
    return o instanceof Currency && ((Currency) o).id == id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }
}
