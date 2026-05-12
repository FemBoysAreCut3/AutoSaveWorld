package autosaveworld.features.worldregen.plugins;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardDataProvider extends autosaveworld.features.worldregen.plugins.DataProvider {

	public WorldGuardDataProvider(World world) throws Throwable {
		super(world);
	}

	@Override
	protected void init() {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager manager = container.get(BukkitAdapter.adapt(world));

		if (manager != null) {
			for (ProtectedRegion region : manager.getRegions().values()) {
				if (region instanceof GlobalProtectedRegion) {
					continue;
				}
				addChunksInBounds(
						region.getMinimumPoint().getBlockX(),
						region.getMinimumPoint().getBlockZ(),
						region.getMaximumPoint().getBlockX(),
						region.getMaximumPoint().getBlockZ()
				);
			}
		}
	}

}