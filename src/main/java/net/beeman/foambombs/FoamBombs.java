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

	// Instantiate the block copying settings from standard TNT and setting the ID key
	public static final Block WATER_TNT_REGISTRY = new WaterTntBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.TNT).setId(WATER_TNT_BLOCK_KEY)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Foam Bombs mod...");

		// Register block and block item using their keys
		Registry.register(BuiltInRegistries.BLOCK, WATER_TNT_BLOCK_KEY, WATER_TNT_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, WATER_TNT_ITEM_KEY, new BlockItem(WATER_TNT_REGISTRY, new Item.Properties().setId(WATER_TNT_ITEM_KEY)));

		// Add to creative tab
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
			content.accept(WATER_TNT_REGISTRY);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
