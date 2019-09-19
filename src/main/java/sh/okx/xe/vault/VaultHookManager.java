package sh.okx.xe.vault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import sh.okx.xe.XePlugin;

public class VaultHookManager {
  private final XePlugin plugin;
  private XeVaultEconomy economy;

  public VaultHookManager(XePlugin plugin) {
    this.plugin = plugin;
  }

  public void hook() {
    try {
      if (this.economy == null) {
        this.economy = new XeVaultEconomy(plugin);
      }

      final ServicesManager sm = plugin.getServer().getServicesManager();
      sm.register(Economy.class, this.economy, plugin, ServicePriority.High);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void unhook() {
    final ServicesManager sm = plugin.getServer().getServicesManager();

    if (this.economy != null) {
      sm.unregister(Economy.class, this.economy);
      this.economy = null;
    }
  }

}