package me.chocolf.moneyfrommobs.runnables;

import java.util.function.Consumer;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.chocolf.moneyfrommobs.MoneyFromMobs;
import me.chocolf.moneyfrommobs.managers.MultipliersManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class RepeatingMultiplierEvent implements Consumer<ScheduledTask> {

	private final MoneyFromMobs plugin;

	public RepeatingMultiplierEvent(MoneyFromMobs plugin) {
		this.plugin = plugin;
	}

	@Override
	public void accept(ScheduledTask scheduledTask) {
		MultipliersManager multipliersManager = plugin.getMultipliersManager();
		if (multipliersManager.getCurrentMultiplierEvent() == null){
			multipliersManager.setEventMultiplier(multipliersManager.getRepeatingMultiplier());

			// send multiplier event started message
			Bukkit.getConsoleSender().sendMessage(multipliersManager.getRepeatingStartMessage());
			for (Player p : Bukkit.getServer().getOnlinePlayers()){
				if (!p.hasMetadata("MfmMuteMessages")) {
					p.sendMessage(multipliersManager.getRepeatingStartMessage());
				}
			}

			// run task later to set multiplier back to 0 and send message to players
			ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> {
				multipliersManager.setEventMultiplier(0);
				multipliersManager.setCurrentMultiplierEvent(null, 0);

				Bukkit.getConsoleSender().sendMessage(multipliersManager.getRepeatingEndMessage());
				for (Player p : Bukkit.getServer().getOnlinePlayers()){
					if (!p.hasMetadata("MfmMuteMessages"))
						p.sendMessage(multipliersManager.getRepeatingEndMessage());
				}
			}, multipliersManager.getRepeatingDuration() * 20L);
			multipliersManager.setCurrentMultiplierEvent(task, multipliersManager.getRepeatingDuration());
		}
	}
}
