package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file

@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "lucktable";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final DeferredBlock<LuckTableBlock> LUCK_TABLE = BLOCKS.register("luck_table",
            () -> new LuckTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .lightLevel(state -> 7)));

    public static final DeferredItem<BlockItem> LUCK_TABLE_ITEM =
            ITEMS.registerSimpleBlockItem("luck_table", LUCK_TABLE);


    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> LUCK_TABLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(LUCK_TABLE_ITEM.get());// Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModLootModifiers.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);


        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        ;

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }


        // You can use SubscribeEvent and let the Event Bus discover methods to call
        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            // Do something when the server starts
            LOGGER.info("HELLO from server starting");
        }

        @SubscribeEvent
        public void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide()) return;      // nur auf dem Server
            if (player.tickCount % 20 != 0) return;         // nur einmal pro Sekunde prüfen

            int stage = player.getData(ModAttachments.LUCK_STAGE); // 0, 1, 2 oder 3
            int level = stage * 3;                                  // ergibt Luck 0, 3, 6 oder 9
            if (level <= 0) {
                player.removeEffect(MobEffects.LUCK);
                return;
            }

            MobEffectInstance current = player.getEffect(MobEffects.LUCK);
            if (current == null || current.getAmplifier() != level - 1 || !current.isInfiniteDuration()) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.LUCK,
                        MobEffectInstance.INFINITE_DURATION, // läuft nie ab
                        level - 1,                           // Stufe 5 = Verstärker 4
                        true,                                // ambient
                        false,                               // keine Partikel
                        true));                              // Symbol anzeigen
            }
        }

        // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
        @EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
        static class ClientModEvents {
            @SubscribeEvent
            static void onClientSetup(FMLClientSetupEvent event) {
                // Some client setup code
                LOGGER.info("HELLO FROM CLIENT SETUP");
                LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            }
        }
}


