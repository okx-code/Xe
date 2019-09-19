package sh.okx.xe.data;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.OptionalContextResolver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyManager {
  private static CurrencyManager instance;
  private final List<Currency> currencies = new ArrayList<>();
  @Getter
  private Currency primaryCurrency;

  public static synchronized CurrencyManager getInstance() {
    if (instance == null) {
      instance = new CurrencyManager();
    }
    return instance;
  }

  public void addCurrency(Currency currency) {
    currencies.add(currency);
  }

  public void setPrimaryCurrency(Currency primaryCurrency) {
    this.primaryCurrency = primaryCurrency;
  }

  @Nullable
  public Currency getCurrency(int id) {
    return currencies.stream()
        .filter(c -> c.getId() == id)
        .findFirst()
        .orElse(null);
  }

  @Nullable
  public Currency getCurrency(@NotNull String name) {
    try {
      Currency byId = getCurrency(Integer.parseInt(name));
      if (byId != null) {
        return byId;
      }
    } catch (NumberFormatException ignored) {
    }

    return currencies
        .stream()
        .filter(c -> name.equalsIgnoreCase(c.getMajor()) || name.equalsIgnoreCase(c.getMajorPlural()))
        .findFirst()
        .orElse(null);
  }

  public List<Currency> getCurrencies() {
    return currencies;
  }

  public List<String> getCurrencyNames() {
    return currencies.stream()
        .map(Currency::getMajor)
        .collect(Collectors.toList());
  }

  public OptionalContextResolver<Currency, BukkitCommandExecutionContext> getContextResolver() {
    return c -> {
      String s = c.popFirstArg();
      if (s == null) {
        return c.hasFlag("nullable") ? null : primaryCurrency;
      }
      Currency currency = getCurrency(s);
      if (currency == null) {
        throw new InvalidCommandArgument("Currency not found");
      }
      return currency;
    };
  }
}
