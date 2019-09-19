package sh.okx.xe.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SqlDatabase implements Database {
  public abstract Connection getConnection() throws SQLException;
}
