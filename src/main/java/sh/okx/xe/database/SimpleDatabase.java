package sh.okx.xe.database;

import sh.okx.xe.database.dao.PlayerBalanceDao;

public class SimpleDatabase implements Database {
  private final PlayerBalanceDao dao;

  public SimpleDatabase(PlayerBalanceDao dao) {
    this.dao = dao;
  }

  @Override
  public PlayerBalanceDao getPlayerBalanceDao() {
    return dao;
  }
}
