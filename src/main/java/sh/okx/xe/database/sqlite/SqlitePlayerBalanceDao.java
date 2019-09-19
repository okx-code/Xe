package sh.okx.xe.database.sqlite;

import lombok.RequiredArgsConstructor;
import sh.okx.xe.data.Currency;
import sh.okx.xe.data.CurrencyManager;
import sh.okx.xe.data.PlayerBalance;
import sh.okx.xe.database.dao.PlayerBalanceDao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public class SqlitePlayerBalanceDao implements PlayerBalanceDao {
  private final SqliteDatabase database;
  private final boolean perWorld;

  @Override
  public void init() {
    try {
      Connection connection = database.getConnection();
      connection.createStatement().execute("CREATE TABLE IF NOT EXISTS balances (" +
          "uuidBig INTEGER, " +
          "uuidSmall INTEGER ," +
          "currencyId INTEGER, " +
          "world TEXT, " +
          "amount BLOB, " +
          "infinite BOOLEAN, " +
          "PRIMARY KEY (uuidBig, uuidSmall, currencyId, world))");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public PlayerBalance getPlayerBalance(String world, UUID uuid, Currency currency) {
    Connection connection = database.getConnection();
    try {
      PreparedStatement statement = connection.prepareStatement("SELECT amount, infinite FROM balances WHERE uuidBig = ? AND uuidSmall = ? AND currencyId = ?");
      statement.setLong(1, uuid.getMostSignificantBits());
      statement.setLong(2, uuid.getLeastSignificantBits());
      statement.setInt(3, currency.getId());

      ResultSet results = statement.executeQuery();
      BigDecimal decimal;
      boolean infinite;
      if (results.next()) {
        decimal = new BigDecimal(new BigInteger(results.getBytes(1)), MathContext.DECIMAL128).scaleByPowerOfTen(-9);
        infinite = results.getBoolean(2);
      } else {
        decimal = currency.getDefaultAmount();
        infinite = false;
      }
      return new PlayerBalance("\0", uuid, currency.getId(), decimal, infinite);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean save(PlayerBalance balance) {
    Connection connection = database.getConnection();
    try {
      PreparedStatement statement = connection.prepareStatement("REPLACE INTO balances VALUES (?, ?, ?, ?, ?, ?)");
      statement.setLong(1, balance.getUuid().getMostSignificantBits());
      statement.setLong(2, balance.getUuid().getLeastSignificantBits());
      statement.setInt(3, balance.getCurrencyId());
      statement.setString(4, balance.getWorld());
      statement.setBytes(5, balance.getBalance().scaleByPowerOfTen(9).toBigInteger().toByteArray());
      statement.setBoolean(6, balance.isInfinite());

      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void disable() {
    try {
      database.getConnection().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
