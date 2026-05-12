package autosaveworld.features.purge.weregen;

import org.bukkit.World;
import com.sk89q.worldedit.math.BlockVector3;


public class WorldEditRegeneration {

	private static volatile WorldEditRegenrationInterface instance;

	public static WorldEditRegenrationInterface get() {
		if (instance == null) {
			synchronized (WorldEditRegeneration.class) {
				if (instance == null) {
					instance = new autosaveworld.features.purge.weregen.BukkitAPIWorldEditRegeneration();
				}
			}
		}
		return instance;
	}

	public static interface WorldEditRegenrationInterface {

		public void regenerateRegion(World world, org.bukkit.util.Vector minpoint, org.bukkit.util.Vector maxpoint);

		public void regenerateRegion(World world, BlockVector3 minpoint, BlockVector3 maxpoint);
	}

}