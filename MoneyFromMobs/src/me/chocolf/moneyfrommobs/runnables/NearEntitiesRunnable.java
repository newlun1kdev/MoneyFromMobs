package me.chocolf.moneyfrommobs.runnables;

import java.util.List;
import java.util.function.Consumer;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.chocolf.moneyfrommobs.MoneyFromMobs;

public class NearEntitiesRunnable implements Consumer<ScheduledTask> {

	private final MoneyFromMobs plugin;
	private final int radius;

	public NearEntitiesRunnable(MoneyFromMobs plugin) {
		this.plugin = plugin;
		this.radius = plugin.getConfig().getInt("PickupMoneyWhenInventoryIsFull.Radius");
	}

	@Override
	public void accept(ScheduledTask task) {
		for ( Player p : Bukkit.getOnlinePlayers()) {
			// Dispatch per-player work onto the player's region thread for Folia safety
			p.getScheduler().run(plugin, t -> processPlayer(p), null);
		}
	}

	private void processPlayer(Player p) {
		if (p.getInventory().firstEmpty() != -1) return;
		for ( Entity entity : p.getNearbyEntities(radius, radius, radius)) {
			if (entity instanceof Item) {
				// if player doesn't have permission return
				if (!(p.hasPermission("MoneyFromMobs.use"))) return;

				Item item = (Item) entity;
				ItemStack itemStack = item.getItemStack();

				// if item found is not money return
				if (!plugin.getPickUpManager().isMoneyPickedUp(itemStack)) continue;

				List<String> itemLore = itemStack.getItemMeta().getLore();
				double amount = Double.parseDouble(itemLore.get(1));
				plugin.getPickUpManager().giveMoney(amount, p);
				item.remove();
			}
		}
	}
}
