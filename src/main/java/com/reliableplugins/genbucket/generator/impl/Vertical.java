package com.reliableplugins.genbucket.generator.impl;

import com.reliableplugins.genbucket.GenBucket;
import com.reliableplugins.genbucket.generator.Generator;
import com.reliableplugins.genbucket.generator.data.GeneratorData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class Vertical extends Generator {

    public Vertical(GenBucket plugin) {
        super(plugin);
    }

    @Override
    public void onPlace(GeneratorData data, Player player, Location location) {

        if (!getPlugin().getHookManager().getBuildChecks().canBuild(player, location)) {
            player.sendMessage(ChatColor.RED + "You cannot use a GenBucket here!");
            data.setIndex(getMaxBlocks());
            return;
        }

//        location.getBlock().setType(getMaterial());
//        getPlugin().getNMSHandler().setBlock(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), getMaterial().getId(), (byte) 0);
    }

    @Override
    public void onTick(GeneratorData data) {
        // do checks here
        Block block = data.getWorld().getBlockAt(data.getX(), data.getY() - data.getIndex(), data.getZ());
//        block.setType(getMaterial());

        if (data.getY() - data.getIndex() <= 0) return;

        getPlugin().getNMSHandler().setBlock(data.getWorld(), data.getX(), data.getY() - data.getIndex(), data.getZ(), getMaterial().getId(), (byte) 0);
    }

}
