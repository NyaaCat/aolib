package cat.nyaa.aolib;

import cat.nyaa.aolib.utils.TaskUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class AolibTaskManager {
    private final AoLibPlugin plugin;
    private TickTask tickTask;

    AolibTaskManager(AoLibPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        this.tickTask = new TickTask();
        this.tickTask.runTaskTimer(this.plugin, 0L, 1L);
    }

    public void destructor() {
        if (!this.tickTask.isCancelled()) {
            this.tickTask.cancel();
        }
    }

    public static class TickTask extends BukkitRunnable {
        @Override
        public void run() {
            try {
                TaskUtils.async.onTick();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
