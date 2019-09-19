package sh.okx.xe.command;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import sh.okx.xe.data.Currency;

import java.math.BigDecimal;

//@DynamicCommandAlias("curr")
public class CommandPerCurrency extends XeBaseCommand {
  private final CommandEconomy eco;
  private final Currency currency;

  public CommandPerCurrency(CommandEconomy eco, Currency currency, String name) {
    super(name);
    this.eco = eco;
    this.currency = currency;
  }

  @HelpCommand
  public void onHelp(CommandSender sender, CommandHelp help) {
    help.showHelp();
  }

  @Subcommand("add|give|grant")
  @CommandCompletion("@players")
  @CommandPermission("xe.admin")
  public void onAdd(CommandSender sender, OfflinePlayer player, @Flags("suffixes") BigDecimal amount) {
    eco.onAdd(sender, player, amount, currency);
  }

  @Subcommand("bal|balance|money")
  @Description("Check a player's balance")
  @CommandCompletion("@players")
  public void onBalance(CommandSender sender, OfflinePlayer player) {
    eco.onBalance(sender, player, currency);
  }

  @Subcommand("take|subtract|remove|deduct")
  @CommandCompletion("@players")
  @CommandPermission("xe.admin")
  public void onTake(CommandSender sender, OfflinePlayer player, @Flags("suffixes") BigDecimal amount) {
    eco.onTake(sender, player, amount, currency);
  }

  @Subcommand("set")
  @CommandCompletion("@players")
  @CommandPermission("xe.admin")
  public void onSet(CommandSender sender, OfflinePlayer player, @Flags("suffixes") BigDecimal amount) {
    eco.onSet(sender, player, amount, currency);
  }
}
