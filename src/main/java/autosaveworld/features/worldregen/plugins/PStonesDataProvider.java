package autosaveworld.features.worldregen.plugins;

import org.bukkit.World;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.field.Field;

public class PStonesDataProvider extends autosaveworld.features.worldregen.plugins.DataProvider {

	public PStonesDataProvider(World world) throws Throwable {
		super(world);
	}

	@Override
	protected void init() throws Throwable {
		for (Field field : PreciousStones.getInstance().getForceFieldManager().getFields("*", world)) {
			addChunksInBounds(field.getMinx(), field.getMinz(), field.getMaxx(), field.getMaxz());
		}
	}

}