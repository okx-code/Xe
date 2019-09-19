package sh.okx.xe.database;

import sh.okx.xe.database.dao.PlayerBalanceDao;

public interface Database {
  default void init() {}
  PlayerBalanceDao getPlayerBalanceDao();
}
