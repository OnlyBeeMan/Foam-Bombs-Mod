package net.beeman.foambombs;

import net.beeman.foambombs.block.WaterTntBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoamBombs implements ModInitializer {
	public static final String MOD_ID = "foambombs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Define resource keys for both Block and Item
	public static final ResourceKey<Block> WATER_TNT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("water_tnt"));
	public static final ResourceKey<Item> WATER_TNT_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("water_tnt"));
	
	// Track custom TNT entities in-memory
	public static final java.util.Set<java.util.UUID> WATER_TNT_UUIDS = new java.util.HashSet<>();

	// Define keys for Healing Foam
	public static final ResourceKey<Block> HEALING_FOAM_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("healing_foam"));
	public static final ResourceKey<Item> HEALING_FOAM_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("healing_foam"));

	// Instantiate the blocks
	public static final Block WATER_TNT_REGISTRY = new WaterTntBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.TNT)
			.setId(WATER_TNT_BLOCK_KEY)
	);
	
	public static final Block HEALING_FOAM_REGISTRY = new net.beeman.foambombs.block.HealingFoamBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.POWDER_SNOW)
			.setId(HEALING_FOAM_BLOCK_KEY)
	);

	public static final Item HEALING_FOAM_ITEM_REGISTRY = new net.minecraft.world.item.SolidBucketItem(
		HEALING_FOAM_REGISTRY,
		net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE,
		new Item.Properties().stacksTo(1).setId(HEALING_FOAM_ITEM_KEY)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Foam Bombs mod...");

		// Register Water TNT Block and Item
		Registry.register(BuiltInRegistries.BLOCK, id("water_tnt"), WATER_TNT_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("water_tnt"), new BlockItem(WATER_TNT_REGISTRY, new Item.Properties().setId(WATER_TNT_ITEM_KEY)));
		
		// Register Healing Foam Block and Bucket Item
		Registry.register(BuiltInRegistries.BLOCK, id("healing_foam"), HEALING_FOAM_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("healing_foam"), HEALING_FOAM_ITEM_REGISTRY);

		// Add items to Creative Menu
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
			content.accept(WATER_TNT_REGISTRY);
			content.accept(HEALING_FOAM_ITEM_REGISTRY);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
