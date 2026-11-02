package com.example.examplemod;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class LuckBonusModifier extends LootModifier {

    public static final MapCodec<LuckBonusModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, LuckBonusModifier::new));

    // Extra-Items pro Luck-Punkt (1 = Luck 3 gibt +3, Luck 6 gibt +6, Luck 9 gibt +9)
    private static final int ITEMS_PER_LUCK = 1;

    public LuckBonusModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // Wer bekommt den Bonus? Zuerst herausfinden, ob ein Spieler beteiligt ist.
        Player player = null;
        if (context.hasParam(LootContextParams.BLOCK_STATE)) {
            // Ein Block wurde abgebaut
            if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player p) {
                player = p;
            }
        } else if (context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
            // Ein Mob ist gestorben
            player = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
        }
        if (player == null) {
            return generatedLoot; // kein Spieler, kein Bonus
        }

        // Luck-Wert des Spielers (z. B. 3, 6 oder 9), abgerundet auf eine ganze Zahl
        int bonus = (int) Math.floor(player.getAttributeValue(Attributes.LUCK)) * ITEMS_PER_LUCK;
        if (bonus <= 0) {
            return generatedLoot;
        }

        // Zu jedem Drop die Extra-Items hinzufügen
        ObjectArrayList<ItemStack> extra = new ObjectArrayList<>();
        for (ItemStack stack : generatedLoot) {
            int remaining = bonus;
            while (remaining > 0) {
                // Nicht mehr als in einen Stapel passt (bei Werkzeugen z. B. 1)
                int amount = Math.min(remaining, stack.getMaxStackSize());
                extra.add(stack.copyWithCount(amount));
                remaining -= amount;
            }
        }
        generatedLoot.addAll(extra);
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}