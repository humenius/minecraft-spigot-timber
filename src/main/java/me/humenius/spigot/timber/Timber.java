package me.humenius.spigot.timber;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <h1>me.humenius.spigot.timber.Timber</h1>
 * <p>A plugin which depletes a whole tree when base block is broken - similarly to the me.humenius.spigot.timber.Timber mod.</p>
 *
 * @author humenius
 * @since 1.0.0
 */
public class Timber extends JavaPlugin implements Listener {

    private static final BukkitScheduler BUKKIT_SCHEDULER = Bukkit.getServer().getScheduler();

    private final Logger log = getLogger();

    private static int damageMultiplicator = 1;

    @Override
    public void onEnable() {
        log.info("Plugin enabled.");

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        log.info("Plugin disabled.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().isSneaking()) {
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

            if (isAxe(item.getType()))
                if (isLog(e.getBlock().getType()))
                    dropTree(e.getBlock(), item);
        }
    }

    private void dropTree(final Block block, final ItemStack item) {
        List<Block> blocks = new ArrayList<>();
        List<Block> leaves = new ArrayList<>();

        for (Block _block = block; !_block.isEmpty(); _block = _block.getRelative(BlockFace.UP)) {

            for (int k = -1; k <= 1; k++) {
                for (int j = -1; j <= 1; j++) {
                    for (int l = -1; l <= 1; l++) {
                        final Block relativeBlock = _block.getRelative(j, k, l);

                        if (isLeaf(relativeBlock.getType()))
                            leaves.add(relativeBlock);
                        else if (isLog(relativeBlock.getType()))
                            blocks.add(relativeBlock);
                    }
                }
            }
        }

        int count = 0;
        for (final Block b : blocks) {
            BUKKIT_SCHEDULER.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    b.breakNaturally(item);
                }
            }, ++count);
        }

        for (final Block b : leaves) {
            BUKKIT_SCHEDULER.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    b.breakNaturally(item);
                }
            }, ++count);
        }

        ItemMeta meta = item.getItemMeta();
        Damageable itemDmg = (Damageable) meta;

        itemDmg.setDamage(itemDmg.getDamage() + (blocks.size() + leaves.size()));
        item.setItemMeta(meta);
    }


    private static boolean isLeaf(Material material) {
        switch (material) {
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                return true;
            default:
                return false;
        }
    }

    private static boolean isLog(Material material) {
        switch (material) {
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
                return true;
            default:
                return false;
        }
    }

    private static boolean isAxe(Material material) {
        switch (material) {
            case WOODEN_AXE:
                damageMultiplicator = 25;
                return true;
            case STONE_AXE:
                damageMultiplicator = 20;
                return true;
            case IRON_AXE:
                damageMultiplicator = 15;
                return true;
            case GOLDEN_AXE:
                damageMultiplicator = 5;
                return true;
            case DIAMOND_AXE:
                damageMultiplicator = 10;
                return true;
            default:
                return false;
        }
    }

}
