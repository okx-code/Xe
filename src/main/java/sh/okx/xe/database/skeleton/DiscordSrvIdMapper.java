package sh.okx.xe.database.skeleton;

import github.scarsz.discordsrv.DiscordSRV;
import java.util.UUID;

public class DiscordSrvIdMapper implements PlayerIdMapper {
  @Override
  public String getId(UUID uuid) {
    return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
  }
}
