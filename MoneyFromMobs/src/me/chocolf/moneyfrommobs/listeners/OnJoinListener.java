package me.chocolf.moneyfrommobs.listeners;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.chocolf.moneyfrommobs.MoneyFromMobs;
import me.chocolf.moneyfrommobs.managers.MessageManager;
import me.chocolf.moneyfrommobs.utils.UpdateChecker;

public class OnJoinListener implements Listener{

	MoneyFromMobs plugin;

	public OnJoinListener(MoneyFromMobs plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Bukkit.getAsyncScheduler().runNow(plugin, task -> {
			Player p = e.getPlayer();
			if (p.isOp() && UpdateChecker.checkForUpdate()) {
				// Send messages on player's region thread for Folia safety
				p.getScheduler().run(plugin, t -> {
					p.sendMessage("");
					p.sendMessage(MessageManager.applyColour("&aUpdate Available for &lMoneyFromMobs&a: "));
					p.sendMessage(MessageManager.applyColour("https://www.spigotmc.org/resources/money-from-mobs.79137/"));
					p.sendMessage("");
				}, null);
			}
		});
	}
}
