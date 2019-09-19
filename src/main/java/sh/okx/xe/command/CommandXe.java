package sh.okx.xe.command;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import sh.okx.xe.XePlugin;
import sh.okx.xe.database.dao.PlayerBalanceDao;
import sh.okx.xe.migration.Migration;

/**
 * administrative commands
 */
@CommandAlias("xe|xem|xemanage")
@Description("Xe management commands")
@CommandPermission("xe.admin")
public class CommandXe extends XeBaseCommand {
  @Dependency
  private XePlugin plugin;
  @Dependency
  private PlayerBalanceDao dao;

  @HelpCommand
  public void onHelp(CommandSender sender, CommandHelp help) {
    help.showHelp();
  }

  @Subcommand("migrate")
  @Description("Valid migrations: Craftconomy")
  public void onMigrate(CommandSender sender, Migration migration) {
    sender.sendMessage("Migrating: " + migration.getClass().getSimpleName());
    migration.runMigration(dao, (type, message) -> sender.sendMessage("> " + message));
  }
}
