package wacky.storagesign;

import com.github.teruteru128.logger.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class SignPhysicsEvent implements Listener {

	StorageSignCore plugin;
  private Logger _logger;

    public SignPhysicsEvent(StorageSignCore plugin, Logger logger) {
    	this.plugin = plugin;
      this._logger = logger;

      plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        event.setCancelled(plugin.isStorageSign(event.getBlock()));
    }
}
