package software.bigbade.playervaults.mysql.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import software.bigbade.playervaults.PlayerVaults;
import software.bigbade.playervaults.loading.ExternalDataLoader;

public final class MySQLUtils {
  private static final String PARAM_ERROR = "Error setting statement param";

  private MySQLUtils() {}

  public static void safeClose(AutoCloseable closeable) {
    try {
      closeable.close();
    } catch (Exception e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE,
                                         "Error closing connection", e);
    }
  }

  public static PreparedStatement getStatement(Connection connection,
                                               String statement) {
    try {
      return connection.prepareStatement(statement);
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE,
                                         "Error creating statement", e);
      return null;
    }
  }

  public static void setString(PreparedStatement statement, String string) {
    try {
      statement.setString(1, string);
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE, PARAM_ERROR, e);
    }
  }

  public static void setInt(PreparedStatement statement, int integer) {
    try {
      statement.setInt(2, integer);
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE, PARAM_ERROR, e);
    }
  }

  public static void setBytes(PreparedStatement statement, byte[] bytes) {
    try {
      statement.setBytes(3, bytes);
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE, PARAM_ERROR, e);
    }
  }

  public static byte[] getBytes(ResultSet result) {
    try {
      return result.getBytes(1);
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE, PARAM_ERROR, e);
      return new byte[0];
    }
  }

  public static ResultSet getResults(PreparedStatement statement) {
    try {
      return statement.executeQuery();
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE, "Error executing query",
                                         e);
      return null;
    }
  }

  public static void executeUpdate(PreparedStatement statement) {
    try {
      statement.executeUpdate();
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE,
                                         "Error updating database", e);
    }
  }

  public static boolean isClosed(Statement statement) {
    try {
      return statement.isClosed();
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(
          Level.SEVERE, "Error checking status of statement", e);
      return true;
    }
  }

  public static boolean next(ResultSet result) {
    try {
      return result.next();
    } catch (SQLException e) {
      ExternalDataLoader.getLogger().log(Level.SEVERE,
                                         "Error advancing result set", e);
      return false;
    }
  }
}