package com.example.examplemod;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ExampleMod.MODID);

    // Der Merkzettel: speichert die Stufe (0 bis 3) pro Spieler
    public static final Supplier<AttachmentType<Integer>> LUCK_STAGE = ATTACHMENT_TYPES.register(
            "luck_stage",
            () -> AttachmentType.builder(() -> 0)               // Startwert 0
                    .serialize(Codec.INT)                       // wird im Spielstand gespeichert
                    .copyOnDeath()                              // bleibt nach dem Tod erhalten
                    .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}