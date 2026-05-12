package autosaveworld.features.purge.plugins.wg;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import autosaveworld.core.AutoSaveWorld;
import autosaveworld.core.logging.MessageLogger;
import autosaveworld.features.purge.ActivePlayersList;
import autosaveworld.features.purge.DataPurge;
import autosaveworld.features.purge.taskqueue.TaskExecutor;

public class WGPurge extends DataPurge {

	public WGPurge(ActivePlayersList activelist) {
		super("WorldGuard", activelist);
	}

	@Override
	public void doPurge() {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		try (TaskExecutor queue = new TaskExecutor(30)) {
			for (World w : Bukkit.getWorlds()) {
				MessageLogger.debug("Checking WG protections in world " + w.getName());

				RegionManager regionmanager = container.get(BukkitAdapter.adapt(w));
				if (regionmanager == null) {
					continue;
				}

				ArrayList<ProtectedRegion> regions = new ArrayList<ProtectedRegion>(regionmanager.getRegions().values());
				for (ProtectedRegion rg : regions) {
					MessageLogger.debug("Checking region " + rg.getId());

					if (!rg.hasMembersOrOwners()) {
						continue;
					}

					DomainClearTask domainClearTask = new DomainClearTask(rg);
					for (DefaultDomain domain : new DefaultDomain[] {rg.getOwners(), rg.getMembers()}) {
						for (String playerName : domain.getPlayers()) {
							if (!activeplayerslist.isActiveName(playerName)) {
								MessageLogger.debug(playerName + " is inactive");
								domainClearTask.add(playerName);
							}
						}
						for (UUID playerUUID : domain.getUniqueIds()) {
							if (!activeplayerslist.isActiveUUID(playerUUID)) {
								MessageLogger.debug(playerUUID + " is inactive");
								domainClearTask.add(playerUUID);
							}
						}
					}

					if (domainClearTask.getPlayersToClearCount() == (rg.getOwners().size() + rg.getMembers().size())) {
						if (AutoSaveWorld.getInstance().getMainConfig().purgeWGRegenRg) {
							RegionRegenTask regenTask = new RegionRegenTask(w, rg, AutoSaveWorld.getInstance().getMainConfig().purgeWGNoregenOverlap);
							queue.execute(regenTask);
						}

						RegionDeleteTask deleteTask = new RegionDeleteTask(w, rg);
						queue.execute(deleteTask);
						incDeleted();
						continue;
					}

					if (domainClearTask.hasPlayersToClear()) {
						queue.execute(domainClearTask);
						incCleaned();
					}
				}
			}
		}
	}

}