package autosaveworld.features.worldregen.plugins;

import org.bukkit.World;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;

import java.util.Collection;

public class TownyDataProvider extends autosaveworld.features.worldregen.plugins.DataProvider {

	public TownyDataProvider(World world) throws Throwable {
		super(world);
	}

	@Override
	protected void init() {
		TownyWorld townyWorld = TownyAPI.getInstance().getTownyWorld(world.getName());

		if (townyWorld == null) {
			return;
		}

		Collection<TownBlock> townBlocks = townyWorld.getTownBlocks();

		if (townBlocks != null) {
			for (TownBlock tb : townBlocks) {
				addChunkAtCoord(tb.getX(), tb.getZ());
			}
		}
	}
}