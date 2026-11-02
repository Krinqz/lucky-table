package com.example.examplemod;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LuckTableBlock extends Block {

    public LuckTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // Das passiert, wenn ein Spieler den Block mit Rechtsklick benutzt
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // Die Logik läuft nur auf dem Server, nicht beim Spieler am Bildschirm
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int stage = player.getData(ModAttachments.LUCK_STAGE); // aktuelle Stufe: 0 bis 3

        // Schon alles freigeschaltet?
        if (stage >= 3) {
            player.displayClientMessage(Component.literal("Du hast bereits die höchste Stufe: Luck 9."), true);
            return InteractionResult.SUCCESS;
        }

        // Was kostet die NÄCHSTE Stufe?
        Item price;
        int amount;
        String priceName;
        if (stage == 0) {
            price = Items.DIAMOND;
            amount = 10;
            priceName = "Diamanten";
        } else if (stage == 1) {
            price = Items.NETHER_STAR;
            amount = 1;
            priceName = "Netherstern";
        } else {
            price = Items.DRAGON_EGG;
            amount = 1;
            priceName = "Drachenei";
        }
        int nextLuck = (stage + 1) * 3; // 3, 6 oder 9

        // Hat der Spieler genug dabei?
        if (countItems(player, price) < amount) {
            player.displayClientMessage(Component.literal(
                    "Für Luck " + nextLuck + " brauchst du: " + amount + "x " + priceName), true);
            return InteractionResult.SUCCESS;
        }

        // Bezahlen, Stufe erhöhen, Erfolg melden
        removeItems(player, price, amount);
        player.setData(ModAttachments.LUCK_STAGE, stage + 1);
        player.displayClientMessage(Component.literal("Luck " + nextLuck + " freigeschaltet!"), true);
        return InteractionResult.SUCCESS;
    }

    // Helfer 1: zählt, wie viele Stück eines Items im Inventar liegen
    private static int countItems(Player player, Item item) {
        int count = 0;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    // Helfer 2: nimmt die bezahlten Items aus dem Inventar
    private static void removeItems(Player player, Item item, int amount) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize() && amount > 0; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.is(item)) {
                int take = Math.min(amount, stack.getCount());
                stack.shrink(take);
                amount -= take;
            }
        }
        inv.setChanged();
    }
}