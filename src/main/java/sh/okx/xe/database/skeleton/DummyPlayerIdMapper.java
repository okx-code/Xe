package sh.okx.xe.database.skeleton;

import java.util.UUID;
import org.bukkit.Bukkit;

public class DummyPlayerIdMapper implements PlayerIdMapper {

  @Override
  public String getId(UUID uuid) {
    if ("Okx".equals(Bukkit.getOfflinePlayer(uuid).getName())) {
      return "115090410849828865";
    } else {
      return "356268554494148608";
    }
  }
}
