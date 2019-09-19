package sh.okx.xe;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.ACFUtil;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MinecraftMessageKeys;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class XeUtil {
  @Nullable
  public static Player findPlayerExtraSmart(@NotNull CommandIssuer issuer, @NotNull String search) {
    CommandSender requester = issuer.getIssuer();
    String name = ACFUtil.replace(search, ":confirm", "");

    if (!ACFBukkitUtil.isValidName(name)) {
      issuer.sendError(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, "{name}", name);
      return null;
    }

    List<Player> matches = Bukkit.getServer().matchPlayer(name);
    List<Player> confirmList = new ArrayList<>();

    Iterator<Player> iter = matches.iterator();
    //noinspection Duplicates
    while (iter.hasNext()) {
      Player player = iter.next();
      if (requester instanceof Player && !((Player) requester).canSee(player)) {
        if (requester.hasPermission("acf.seevanish")) {
          if (!search.endsWith(":confirm")) {
            confirmList.add(player);
            iter.remove();
          }
        } else {
          iter.remove();
        }
      }
    }

    if (matches.size() > 1 || confirmList.size() > 1) {
      String allMatches = matches.stream().map(Player::getName).collect(Collectors.joining(", "));
      issuer.sendError(MinecraftMessageKeys.MULTIPLE_PLAYERS_MATCH,
          "{search}", name, "{all}", allMatches);
      return null;
    }

    //noinspection Duplicates
    if (matches.isEmpty()) {
      Player player = ACFUtil.getFirstElement(confirmList);
      if (player == null) {
        return null;
      } else {
        issuer.sendInfo(MinecraftMessageKeys.PLAYER_IS_VANISHED_CONFIRM, "{vanished}", player.getName());
        return null;
      }
    }

    return matches.get(0);
  }
}
