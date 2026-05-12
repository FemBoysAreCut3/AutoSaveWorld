package autosaveworld.features.restart;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import autosaveworld.commands.subcommands.StopCommand;
import autosaveworld.core.AutoSaveWorld;
import autosaveworld.core.logging.MessageLogger;
import autosaveworld.utils.SchedulerUtils;
import autosaveworld.utils.Threads.SIntervalTaskThread;

public class CrashRestartThread extends SIntervalTaskThread {

	private final Thread bukkitMainThread;
	protected long syncticktime = 0;

	public CrashRestartThread(Thread mainthread) {
		super("CrashRestartThread");
		this.bukkitMainThread = mainthread;
	}

	@Override
	protected void onStart() {
		int delay = AutoSaveWorld.getInstance().getMainConfig().restartOnCrashCheckerStartDelay;
		MessageLogger.debug("Delaying crashrestart checker start for " + delay + " seconds");
		try {
			Thread.sleep(delay * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		SchedulerUtils.scheduleSyncRepeatingTask(new Runnable() {
			@Override
			public void run() {
				syncticktime = System.currentTimeMillis();
			}
		}, 0, 20);
	}

	@Override
	public boolean isEnabled() {
		long diff = System.currentTimeMillis() - syncticktime;
		return (AutoSaveWorld.getInstance().getMainConfig().restartOncrashEnabled) &&
				(syncticktime != 0) &&
				(diff >= AutoSaveWorld.getInstance().getMainConfig().restartOnCrashTimeout * 1000L);
	}

	@Override
	public void doTask() {
		stopThread();

		Logger log = Bukkit.getLogger();
		log.log(Level.SEVERE, "======================================================");
		log.log(Level.SEVERE, "SERVER CRASH DETECTED: Main thread has stopped responding!");

		List<ThreadInfo> threads = new ArrayList<>(Arrays.asList(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)));
		ThreadInfo mainThreadInfo = extractMainThread(threads);
		if (mainThreadInfo != null) dumpThread(mainThreadInfo, log);

		for (ThreadInfo thread : threads) {
			dumpThread(thread, log);
		}

		if (!AutoSaveWorld.getInstance().getMainConfig().restartJustStop) {
			Runtime.getRuntime().addShutdownHook(new RestartShutdownHook(new File(AutoSaveWorld.getInstance().getMainConfig().restartOnCrashScriptPath)));
		}

		StopCommand.stop();

		log.log(Level.SEVERE, "Emergency Shutdown: Disabling plugins and saving data...");

		Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
		for (int i = plugins.length - 1; i >= 0; i--) {
			try {
				Bukkit.getPluginManager().disablePlugin(plugins[i]);
			} catch (Throwable e) {
				log.log(Level.SEVERE, "Error disabling plugin " + plugins[i].getName(), e);
			}
		}

		try {
			Bukkit.savePlayers();
			for (World w : Bukkit.getWorlds()) {
				log.log(Level.INFO, "Emergency save for world: " + w.getName());
				w.save();
			}
		} catch (Throwable e) {
			log.log(Level.SEVERE, "Failed to save data during crash!", e);
		}

		log.log(Level.SEVERE, "Restarting JVM now.");
		System.exit(1);
	}

	private ThreadInfo extractMainThread(List<ThreadInfo> data) {
		Iterator<ThreadInfo> it = data.iterator();
		while (it.hasNext()) {
			ThreadInfo info = it.next();
			if (info.getThreadId() == bukkitMainThread.threadId()) {
				it.remove();
				return info;
			}
		}
		return null;
	}

	private void dumpThread(ThreadInfo thread, Logger log) {
		log.log(Level.SEVERE, "------------------------------");
		log.log(Level.SEVERE, "Thread: " + thread.getThreadName() + " (ID: " + thread.getThreadId() + ")");
		log.log(Level.SEVERE, "State: " + thread.getThreadState());

		if (thread.getLockedMonitors().length != 0) {
			for (MonitorInfo monitor : thread.getLockedMonitors()) {
				log.log(Level.SEVERE, "\tLocked on: " + monitor.getLockedStackFrame());
			}
		}

		for (StackTraceElement stack : thread.getStackTrace()) {
			log.log(Level.SEVERE, "\t\t" + stack);
		}
	}
}