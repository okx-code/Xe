package sh.okx.xe.migration;

import sh.okx.xe.migration.migrations.CraftconomyH2Migration;

import java.util.HashMap;
import java.util.Map;

public class MigrationFactory {
  private static final Map<String, Migration> migrations = new HashMap<>();

  static {
    migrations.put("craftconomy", new CraftconomyH2Migration());
  }

  public static Migration get(String name) {
    return migrations.get(name.toLowerCase());
  }
}
