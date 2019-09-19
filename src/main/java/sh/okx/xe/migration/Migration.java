package sh.okx.xe.migration;

import sh.okx.xe.data.MessageReceiver;
import sh.okx.xe.database.dao.PlayerBalanceDao;

public interface Migration {
  void runMigration(PlayerBalanceDao dao, MessageReceiver msg);
}
