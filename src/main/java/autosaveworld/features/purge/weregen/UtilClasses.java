package autosaveworld.features.purge.weregen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;


public class UtilClasses {

	public static class BlockToPlaceBack {

		private final Object position;
		private final Object block;

		public BlockToPlaceBack(Object position, Object block) {
			this.position = position;
			this.block = block;
		}

		public Object getPosition() {
			return position;
		}

		public Object getBlock() {
			return block;
		}
	}

	public static class ItemSpawnListener implements Listener {

		@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
		public void onItemSpawn(ItemSpawnEvent event) {
			event.setCancelled(true);
		}
	}
}