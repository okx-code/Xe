package sh.okx.xe.papi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import sh.okx.xe.XePlugin;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.CurrencyManager;

@RequiredArgsConstructor
public class XeExpansion extends PlaceholderExpansion {
  private static final String BALANCE_PREFIX = "balance_";

  private final XePlugin plugin;

  @Override
  @Nullable
  public String onPlaceholderRequest(Player p, String params) {
    if (params.startsWith(BALANCE_PREFIX)) {
      String currencyName = params.substring(BALANCE_PREFIX.length());
      Currency currency = CurrencyManager.getInstance().getCurrency(currencyName);
      return plugin.getDatabase().getPlayerBalanceDao().getPlayerBalance(null, p.getUniqueId(), currency).getBalancedFormatted();
    }
    return null;
  }

  @Override
  public String getIdentifier() {
    return plugin.getDescription().getName();
  }

  @Override
  public String getAuthor() {
    return plugin.getDescription().getAuthors().get(0);
  }

  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }
}
