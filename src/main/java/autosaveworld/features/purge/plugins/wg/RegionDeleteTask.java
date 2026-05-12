package autosaveworld.features.purge.plugins.wg;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import autosaveworld.core.logging.MessageLogger;
import autosaveworld.features.purge.taskqueue.Task;

public class RegionDeleteTask implements Task {

	private World world;
	private ProtectedRegion region;

	public RegionDeleteTask(World world, ProtectedRegion region) {
		this.world = world;
		this.region = region;
	}

	@Override
	public boolean doNotQueue() {
		return false;
	}

	@Override
	public void performTask() {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(world));

		if (rm != null) {
			MessageLogger.debug("Deleting region " + region.getId());
			rm.removeRegion(region.getId());
		} else {
			MessageLogger.debug("Could not find RegionManager for world " + world.getName());
		}
	}

}