package sh.okx.xe.command;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import sh.okx.xe.data.Currency;

public class CommandBalance extends XeBaseCommand {
  private final CommandEconomy eco;
  private final Currency currency;

  public CommandBalance(CommandEconomy eco, Currency currency, String name) {
    super(name);
    this.eco = eco;
    this.currency = currency;
  }

  @Default
  @CommandCompletion("@players")
  public void onBalance(CommandSender sender, OfflinePlayer player) {
    eco.onBalance(sender, player, currency);
  }
}
