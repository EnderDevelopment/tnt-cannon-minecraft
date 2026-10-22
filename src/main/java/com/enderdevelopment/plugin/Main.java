package com.enderdevelopment.plugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.STONE_BUTTON) {
                BlockFace direction = getCannonDirection(clickedBlock);
                if (direction != null && isCannonStructure(clickedBlock, direction)) {
                    Player player = event.getPlayer();
                    if (player.getInventory().contains(Material.GUNPOWDER) && player.getInventory().contains(Material.TNT)) {
                        shootFireball(clickedBlock, direction);
                        player.sendMessage("Fireball launched!");
                    } else {
                        player.sendMessage("You need gunpowder and TNT in your inventory to shoot the cannon.");
                    }
                }
            }
        }
    }

    private BlockFace getCannonDirection(Block buttonBlock) {
        for (BlockFace face : BlockFace.values()) {
            if (face.isCartesian()) {
                Block relativeBlock = buttonBlock.getRelative(face);
                if (relativeBlock.getType() == Material.BLACK_WOOL) {
                    return face;
                }
            }
        }
        return null;
    }

    private boolean isCannonStructure(Block buttonBlock, BlockFace direction) {
        Block woolBlock1 = buttonBlock.getRelative(direction.getOppositeFace());
        Block woolBlock2 = woolBlock1.getRelative(direction.getOppositeFace());
        return woolBlock1.getType() == Material.BLACK_WOOL && woolBlock2.getType() == Material.BLACK_WOOL;
    }

    private void shootFireball(Block buttonBlock, BlockFace direction) {
        Vector directionVector = direction.getDirection();
        Block frontBlock = buttonBlock.getRelative(direction.getOppositeFace().getOppositeFace().getOppositeFace());
        Fireball fireball = frontBlock.getWorld().spawn(frontBlock.getLocation().add(0.5, 0.5, 0.5), Fireball.class);
        fireball.setDirection(directionVector);
        fireball.setYield(4.0f); // TNT explosive damage
        fireball.setIsIncendiary(false);
        fireball.setShooter(null);
    }
}