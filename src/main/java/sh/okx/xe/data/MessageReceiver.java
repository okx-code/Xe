package sh.okx.xe.data;

import co.aikar.commands.MessageType;

public interface MessageReceiver {
  void sendMessage(MessageType type, String message);
}
