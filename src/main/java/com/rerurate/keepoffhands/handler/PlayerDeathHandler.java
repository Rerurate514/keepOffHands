package com.rerurate.keepoffhands.handler;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathHandler {

    private final Map<UUID, ItemStack> savedOffhand = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = player.getItemBySlot(EquipmentSlot.OFFHAND);
        savedOffhand.put(player.getUUID(), stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack saved = savedOffhand.get(player.getUUID());
        if (saved == null || saved.isEmpty()) {
            return;
        }

        event.getDrops().removeIf(drop -> {
            ItemStack dropStack = drop.getItem();
            return dropStack.getItem() == saved.getItem()
                    && dropStack.getCount() == saved.getCount()
                    && ItemStack.isSameItemSameTags(dropStack, saved);
        });
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        ItemStack saved = savedOffhand.remove(player.getUUID());

        if (saved == null || saved.isEmpty()) {
            return;
        }

        player.setItemSlot(EquipmentSlot.OFFHAND, saved.copy());
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        ItemStack saved = savedOffhand.remove(player.getUUID());

        if (saved == null || saved.isEmpty()) {
            return;
        }

        player.setItemSlot(EquipmentSlot.OFFHAND, saved.copy());
    }
}