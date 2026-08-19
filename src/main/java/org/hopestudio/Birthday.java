package org.hopestudio;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Birthday {
    private final HopeCraft plugin;
    private final Map<UUID, LocalDate> pendingBirthdays = new HashMap<>();
    private final Set<UUID> rewardedPlayers = new HashSet<>(); // 记录已发放礼物的玩家
    private final Random random = new Random();

    public Birthday(HopeCraft plugin) {
        this.plugin = plugin;
    }

    public void setPendingBirthday(Player player, int month, int day) {
        try {
            LocalDate birthday = LocalDate.of(LocalDate.now().getYear(), month, day);
            pendingBirthdays.put(player.getUniqueId(), birthday);
            player.sendMessage(ChatColor.GREEN + "已设置生日为 " + month + "月" + day + "日，请使用 /birthday confirm 确认设置");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "日期格式不正确，请输入有效的月份和日期");
        }
    }

    public boolean confirmBirthday(Player player) {
        UUID uuid = player.getUniqueId();
        LocalDate birthday = pendingBirthdays.get(uuid);
        
        if (birthday == null) {
            player.sendMessage(ChatColor.RED + "请先使用 /birthday set <月> <日> 设置生日");
            return false;
        }

        try {
            if (plugin.hasBirthday(uuid)) {
                player.sendMessage(ChatColor.RED + "你已经设置过生日了，无法重复设置");
                return false;
            }

            plugin.setPlayerBirthday(uuid, birthday);
            pendingBirthdays.remove(uuid);
            player.sendMessage(ChatColor.GREEN + "生日设置成功！你的生日是 " + birthday.getMonthValue() + "月" + birthday.getDayOfMonth() + "日");
            
            // 检查是否是今天生日，如果是则立即庆祝
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DateTimeFormatter.ofPattern("MM-dd"));
            String birthdayStr = birthday.format(DateTimeFormatter.ofPattern("MM-dd"));
            
            if (todayStr.equals(birthdayStr)) {
                // 今天就是生日，立即庆祝
                if (!rewardedPlayers.contains(uuid)) {
                    celebrateBirthday(player, birthday);
                    rewardedPlayers.add(uuid);
                    
                    // 广播生日消息
                    List<String> birthdayPlayers = new ArrayList<>();
                    birthdayPlayers.add(player.getName());
                    broadcastBirthdayMessage(birthdayPlayers);
                }
            }
            
            return true;
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "设置生日时出现错误，请联系管理员");
            e.printStackTrace();
            return false;
        }
    }

    public void checkBirthdays() {
        try {
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DateTimeFormatter.ofPattern("MM-dd"));
            
            List<String> birthdayPlayers = new ArrayList<>();
            
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                LocalDate playerBirthday = plugin.getPlayerBirthday(uuid);
                if (playerBirthday != null) {
                    String birthdayStr = playerBirthday.format(DateTimeFormatter.ofPattern("MM-dd"));
                    if (todayStr.equals(birthdayStr)) {
                        // 今天是玩家的生日
                        birthdayPlayers.add(player.getName());
                        
                        // 检查是否已经发放过礼物
                        if (!rewardedPlayers.contains(uuid)) {
                            celebrateBirthday(player, playerBirthday);
                            rewardedPlayers.add(uuid);
                        }
                    }
                }
            }
            
            // 如果有玩家过生日，则进行整点广播
            if (!birthdayPlayers.isEmpty()) {
                broadcastBirthdayMessage(birthdayPlayers);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("检查生日时出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastBirthdayMessage(List<String> birthdayPlayers) {
        if (birthdayPlayers.isEmpty()) {
            // 不发送没有人生日的消息，保持安静
            return;
        } else {
            String playerList = String.join(", ", birthdayPlayers);
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "🎉 今天是 " + playerList + " 的生日！🎉");
            plugin.getServer().broadcastMessage(ChatColor.YELLOW + "让我们一起祝 " + (birthdayPlayers.size() > 1 ? "他们" : "他") + "生日快乐！");
        }
    }

    public void broadcastBirthdayMessage() {
        try {
            LocalDate today = LocalDate.now();
            String todayStr = today.format(DateTimeFormatter.ofPattern("MM-dd"));
            
            List<String> birthdayPlayers = new ArrayList<>();
            
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                LocalDate playerBirthday = plugin.getPlayerBirthday(uuid);
                if (playerBirthday != null) {
                    String birthdayStr = playerBirthday.format(DateTimeFormatter.ofPattern("MM-dd"));
                    if (todayStr.equals(birthdayStr)) {
                        birthdayPlayers.add(player.getName());
                        
                        // 检查是否已经发放过礼物
                        if (!rewardedPlayers.contains(uuid)) {
                            celebrateBirthday(player, playerBirthday);
                            rewardedPlayers.add(uuid);
                        }
                    }
                }
            }
            
            if (birthdayPlayers.isEmpty()) {
                plugin.getServer().broadcastMessage(ChatColor.YELLOW + "当前没有玩家在过生日。");
            } else {
                broadcastBirthdayMessage(birthdayPlayers);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("强制广播生日信息时出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 每日重置已发放礼物的玩家列表
    public void resetRewardedPlayers() {
        rewardedPlayers.clear();
        plugin.getLogger().info("已重置生日礼物发放记录");
    }

    private void celebrateBirthday(Player player, LocalDate birthday) {
        // 给玩家发送礼物：蛋糕和纪念币
        ItemStack cake = new ItemStack(Material.CAKE);
        
        // 创建纪念币（使用向日葵作为基础材料）
        ItemStack coin = new ItemStack(Material.SUNFLOWER); // 使用向日葵作为基础材料
        ItemMeta coinMeta = coin.getItemMeta();
        if (coinMeta != null) {
            coinMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "HopeCraft纪念币");
            
            // 添加特殊附魔效果使其看起来更独特（仅用于视觉效果，无实际作用）
            coinMeta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
            
            // 添加自定义模型数据（如果客户端有对应的资源包）
            coinMeta.setCustomModelData(10001);
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "玩家: " + ChatColor.WHITE + player.getName());
            lore.add(ChatColor.YELLOW + "生日: " + ChatColor.WHITE + birthday.getMonthValue() + "月" + birthday.getDayOfMonth() + "日");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "生日纪念品");
            lore.add(ChatColor.DARK_GRAY + "来自HopeCraft");
            
            // 从配置文件中获取随机祝福语
            List<String> messages = plugin.getConfig().getStringList("birthday-messages");
            if (messages.isEmpty()) {
                // 如果配置文件中没有消息，则使用默认消息
                messages.add("愿你的每一天都像生日一样美好！");
                messages.add("生日快乐！愿你拥有无尽的快乐和成功！");
                messages.add("在这个特别的日子里，希望你能感受到所有的爱和温暖！");
            }
            
            String randomMessage = messages.get(random.nextInt(messages.size()));
            lore.add(ChatColor.GREEN + randomMessage);
            
            coinMeta.setLore(lore);
            coin.setItemMeta(coinMeta);
        }

        player.getInventory().addItem(cake, coin);
        player.sendMessage(ChatColor.GREEN + "生日快乐！你收到了生日礼物：蛋糕和HopeCraft纪念币！");
    }
}