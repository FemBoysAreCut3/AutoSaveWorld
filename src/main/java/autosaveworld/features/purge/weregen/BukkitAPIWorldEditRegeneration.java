package autosaveworld.features.purge.weregen;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.math.BlockVector3;

import autosaveworld.core.logging.MessageLogger;
import autosaveworld.features.purge.weregen.UtilClasses.BlockToPlaceBack;
import autosaveworld.features.purge.weregen.UtilClasses.ItemSpawnListener;
import autosaveworld.features.purge.weregen.WorldEditRegeneration.WorldEditRegenrationInterface;
import autosaveworld.utils.BukkitUtils;

public class BukkitAPIWorldEditRegeneration implements WorldEditRegenrationInterface {

	private ItemSpawnListener itemremover = new ItemSpawnListener();

	@Override
	public void regenerateRegion(World world, BlockVector3 minpoint, BlockVector3 maxpoint) {
		Vector bukkitMin = new Vector(minpoint.getX(), minpoint.getY(), minpoint.getZ());
		Vector bukkitMax = new Vector(maxpoint.getX(), maxpoint.getY(), maxpoint.getZ());
		regenerateRegion(world, bukkitMin, bukkitMax);
	}

	@Override
	public void regenerateRegion(World world, Vector minpoint, Vector maxpoint) {
		int minX = Math.min(minpoint.getBlockX(), maxpoint.getBlockX());
		int minY = Math.min(minpoint.getBlockY(), maxpoint.getBlockY());
		int minZ = Math.min(minpoint.getBlockZ(), maxpoint.getBlockZ());
		int maxX = Math.max(minpoint.getBlockX(), maxpoint.getBlockX());
		int maxY = Math.max(minpoint.getBlockY(), maxpoint.getBlockY());
		int maxZ = Math.max(minpoint.getBlockZ(), maxpoint.getBlockZ());

		LinkedList<BlockToPlaceBack> placeBackQueue = new LinkedList<>();
		BukkitUtils.registerListener(itemremover);

		int worldMinY = world.getMinHeight();
		int worldMaxY = world.getMaxHeight();

		for (int x = (minX >> 4); x <= (maxX >> 4); x++) {
			for (int z = (minZ >> 4); z <= (maxZ >> 4); z++) {
				for (int bx = 0; bx < 16; bx++) {
					for (int bz = 0; bz < 16; bz++) {
						for (int by = worldMinY; by < worldMaxY; by++) {
							int absX = (x << 4) + bx;
							int absZ = (z << 4) + bz;

							if (absX < minX || absX > maxX || absZ < minZ || absZ > maxZ || by < minY || by > maxY) {
								Block block = world.getBlockAt(absX, by, absZ);
								if (block.getType() != Material.AIR) {
									placeBackQueue.add(new BlockToPlaceBack(new Vector(absX, by, absZ), block.getBlockData()));
								}
							}
						}
					}
				}
			}
		}

		for (int x = (minX >> 4); x <= (maxX >> 4); x++) {
			for (int z = (minZ >> 4); z <= (maxZ >> 4); z++) {
				try {
					world.regenerateChunk(x, z);
				} catch (NoSuchMethodError e) {
					fallbackRegeneration(world, x, z);
				}
			}
		}

		processPlaceBack(world, placeBackQueue);
		BukkitUtils.unregisterListener(itemremover);
	}

	private void fallbackRegeneration(World world, int x, int z) {
		MessageLogger.debug("Standard regeneration failed for chunk " + x + " " + z + ". Manual reset required.");
	}

	private void processPlaceBack(World world, LinkedList<BlockToPlaceBack> queue) {
		Iterator<BlockToPlaceBack> it = queue.iterator();
		while (it.hasNext()) {
			BlockToPlaceBack entry = it.next();
			Vector pos = (Vector) entry.getPosition();
			BlockData data = (BlockData) entry.getBlock();

			world.setBlockData(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), data);
			it.remove();
		}
	}
}