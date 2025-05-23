package com.soy.soycheese;

import com.mojang.logging.LogUtils;
import com.soy.soycheese.capability.foodlist.PlayerFoodList;
import com.soy.soycheese.capability.foodlist.PlayerFoodListProvider;
import com.soy.soycheese.capability.skilllist.PlayerSkillList;
import com.soy.soycheese.capability.skilllist.PlayerSkillListProvider;
import com.soy.soycheese.client.packet.PageClientPacket;
import com.soy.soycheese.handle.PlayerMixinInterface;
import com.soy.soycheese.network.SoycheesePacket;
import com.soy.soycheese.registries.BlockRegistry;
import com.soy.soycheese.registries.ItemRegistry;
import com.soy.soycheese.registries.MenuRegistry;
import com.soy.soycheese.registries.SkillRegistry;
import com.soy.soycheese.tracking.ModEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SoycheeseCore.MODID)
public class SoycheeseCore
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "soycheese_core";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    //public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    //public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
    /*
    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("mutton", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));
    public static final RegistryObject<Item> EXAMPLE_ITEM2 = ITEMS.register("enchanted_golden_apple", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));
    */
    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    /*public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.ENCHANTEDGOLDENAPPLE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.ENCHANTEDGOLDENAPPLE.get());
                // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());*/
    public SoycheeseCore(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the Deferred Register to the mod event bus so blocks get registered
        //BLOCKS.register(modEventBus);
        BlockRegistry.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        //ITEMS.register(modEventBus);
        ItemRegistry.register(modEventBus);
        SkillRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,this::attachCapability);

        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedIn);

        SoycheesePacket.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SoycheeeCoreClient::init);
    }

    public void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player player)
        {
            if(!player.getCapability(PlayerFoodListProvider.PLAYER_FOOD_LIST_CAPABILITY).isPresent())
            {
                event.addCapability(new ResourceLocation(MODID, PlayerFoodList.NBT_KEY_FOOD_LIST),new PlayerFoodListProvider());
            }
            if(!player.getCapability(PlayerSkillListProvider.PLAYER_SKILL_LIST_CAPABILITY).isPresent())
            {
                event.addCapability(new ResourceLocation(MODID, PlayerSkillList.NBT_KEY_SKILL_LIST),new PlayerSkillListProvider());
            }
        }
    }

    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SoycheesePacket.sendToClient(new PageClientPacket(((PlayerMixinInterface) serverPlayer).getTutorialPages()), serverPlayer);
        }
    }
}
