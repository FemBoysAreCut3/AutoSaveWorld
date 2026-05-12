package autosaveworld.features.purge.plugins.wg;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import autosaveworld.core.logging.MessageLogger;
import autosaveworld.features.purge.taskqueue.Task;
import autosaveworld.features.purge.weregen.WorldEditRegeneration;

public class RegionRegenTask implements Task {

	private World world;
	private ProtectedRegion region;
	private boolean noregenoverlap;

	public RegionRegenTask(World world, ProtectedRegion region, boolean noregenoverlap) {
		this.world = world;
		this.region = region;
		this.noregenoverlap = noregenoverlap;
	}

	@Override
	public boolean doNotQueue() {
		return true;
	}

	@Override
	public void performTask() {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(world));

		if (rm != null) {
			if (!(noregenoverlap && (rm.getApplicableRegions(region).size() > 1))) {
				MessageLogger.debug("Regenerating region " + region.getId());
				WorldEditRegeneration.get().regenerateRegion(world, region.getMinimumPoint(), region.getMaximumPoint());
			}
		} else {
			MessageLogger.debug("Could not find RegionManager for world " + world.getName());
		}
	}

}