package sh.okx.xe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.locales.MessageKeyProvider;
import org.bukkit.command.CommandSender;

public abstract class XeBaseCommand extends BaseCommand {
  public XeBaseCommand(String cmd) {
    super(cmd);
  }

  public XeBaseCommand() {
  }

  void sendMsg(MessageType type, MessageKeyProvider key, String... replacements) {
    getCurrentCommandIssuer().sendMessage(type, key.getMessageKey(), replacements);
  }

  void sendMsg(CommandSender sender, MessageType type, MessageKeyProvider key, String... replacements) {
    getCurrentCommandManager().getCommandIssuer(sender).sendMessage(type, key.getMessageKey(), replacements);
  }
}
