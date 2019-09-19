package sh.okx.xe.database.sqlite;

import lombok.Getter;
import lombok.SneakyThrows;
import sh.okx.xe.database.SqlDatabase;
import sh.okx.xe.database.dao.CachedForwardingPlayerBalanceDao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteDatabase extends SqlDatabase {
  private final boolean perWorld;
  @Getter
  private final Connection connection;
  @Getter
  private CachedForwardingPlayerBalanceDao playerBalanceDao;

  @SneakyThrows(SQLException.class)
  public SqliteDatabase(File dataFolder, boolean perWorld) {
    this.perWorld = perWorld;
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }

    File databaseFile = new File(dataFolder, "data.db");
    connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
  }

  @Override
  public void init() {
    playerBalanceDao = new CachedForwardingPlayerBalanceDao(new SqlitePlayerBalanceDao(this, perWorld));
    playerBalanceDao.init();
  }
}
