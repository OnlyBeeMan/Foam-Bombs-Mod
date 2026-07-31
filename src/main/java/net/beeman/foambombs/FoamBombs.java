package net.beeman.foambombs;

import com.mojang.serialization.MapCodec;
import net.beeman.foambombs.block.HealingFoamBlock;
import net.beeman.foambombs.block.HealingFoamTntBlock;
import net.beeman.foambombs.block.InvisibilityFoamBlock;
import net.beeman.foambombs.block.InvisibilityFoamTntBlock;
import net.beeman.foambombs.block.PoisonFoamBlock;
import net.beeman.foambombs.block.PoisonFoamTntBlock;
import net.beeman.foambombs.recipe.FoamTntRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoamBombs implements ModInitializer {
	public static final String MOD_ID = "foambombs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Healing Foam & TNT
	public static final ResourceKey<Block> HEALING_FOAM_TNT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("healing_foam_tnt"));
	public static final ResourceKey<Item> HEALING_FOAM_TNT_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("healing_foam_tnt"));
	public static final ResourceKey<Block> HEALING_FOAM_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("healing_foam"));
	public static final ResourceKey<Item> HEALING_FOAM_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("healing_foam"));

	// Invisibility Foam & TNT
	public static final ResourceKey<Block> INVISIBILITY_FOAM_TNT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("invisibility_foam_tnt"));
	public static final ResourceKey<Item> INVISIBILITY_FOAM_TNT_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("invisibility_foam_tnt"));
	public static final ResourceKey<Block> INVISIBILITY_FOAM_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("invisibility_foam"));
	public static final ResourceKey<Item> INVISIBILITY_FOAM_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("invisibility_foam"));

	// Poison Foam & TNT
	public static final ResourceKey<Block> POISON_FOAM_TNT_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("poison_foam_tnt"));
	public static final ResourceKey<Item> POISON_FOAM_TNT_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("poison_foam_tnt"));
	public static final ResourceKey<Block> POISON_FOAM_BLOCK_KEY = ResourceKey.create(Registries.BLOCK, id("poison_foam"));
	public static final ResourceKey<Item> POISON_FOAM_ITEM_KEY = ResourceKey.create(Registries.ITEM, id("poison_foam"));

	// Recipe Serializer
	public static final RecipeSerializer<FoamTntRecipe> FOAM_TNT_RECIPE_SERIALIZER = new RecipeSerializer<>(
		MapCodec.unit(new FoamTntRecipe()),
		StreamCodec.unit(new FoamTntRecipe())
	);

	// Track custom TNT entities in-memory
	public static final java.util.Set<java.util.UUID> HEALING_FOAM_TNT_UUIDS = new java.util.HashSet<>();

	// Instantiate Healing Foam blocks and items
	public static final Block HEALING_FOAM_TNT_REGISTRY = new HealingFoamTntBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.TNT).setId(HEALING_FOAM_TNT_BLOCK_KEY)
	);
	public static final Block HEALING_FOAM_REGISTRY = new HealingFoamBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.POWDER_SNOW).setId(HEALING_FOAM_BLOCK_KEY)
	);
	public static final Item HEALING_FOAM_ITEM_REGISTRY = new SolidBucketItem(
		HEALING_FOAM_REGISTRY, net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE,
		new Item.Properties().stacksTo(1).setId(HEALING_FOAM_ITEM_KEY)
	);

	// Instantiate Invisibility Foam blocks and items
	public static final Block INVISIBILITY_FOAM_TNT_REGISTRY = new InvisibilityFoamTntBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.TNT).setId(INVISIBILITY_FOAM_TNT_BLOCK_KEY)
	);
	public static final Block INVISIBILITY_FOAM_REGISTRY = new InvisibilityFoamBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.POWDER_SNOW).setId(INVISIBILITY_FOAM_BLOCK_KEY)
	);
	public static final Item INVISIBILITY_FOAM_ITEM_REGISTRY = new SolidBucketItem(
		INVISIBILITY_FOAM_REGISTRY, net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE,
		new Item.Properties().stacksTo(1).setId(INVISIBILITY_FOAM_ITEM_KEY)
	);

	// Instantiate Poison Foam blocks and items
	public static final Block POISON_FOAM_TNT_REGISTRY = new PoisonFoamTntBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.TNT).setId(POISON_FOAM_TNT_BLOCK_KEY)
	);
	public static final Block POISON_FOAM_REGISTRY = new PoisonFoamBlock(
		BlockBehaviour.Properties.ofFullCopy(Blocks.POWDER_SNOW).setId(POISON_FOAM_BLOCK_KEY)
	);
	public static final Item POISON_FOAM_ITEM_REGISTRY = new SolidBucketItem(
		POISON_FOAM_REGISTRY, net.minecraft.sounds.SoundEvents.SLIME_BLOCK_PLACE,
		new Item.Properties().stacksTo(1).setId(POISON_FOAM_ITEM_KEY)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Foam Bombs mod...");

		// Register Recipe Serializer
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id("foam_tnt_crafting"), FOAM_TNT_RECIPE_SERIALIZER);

		// Register Healing Foam TNT & Block
		Registry.register(BuiltInRegistries.BLOCK, id("healing_foam_tnt"), HEALING_FOAM_TNT_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("healing_foam_tnt"), new BlockItem(HEALING_FOAM_TNT_REGISTRY, new Item.Properties().setId(HEALING_FOAM_TNT_ITEM_KEY)));
		Registry.register(BuiltInRegistries.BLOCK, id("healing_foam"), HEALING_FOAM_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("healing_foam"), HEALING_FOAM_ITEM_REGISTRY);

		// Register Invisibility Foam TNT & Block
		Registry.register(BuiltInRegistries.BLOCK, id("invisibility_foam_tnt"), INVISIBILITY_FOAM_TNT_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("invisibility_foam_tnt"), new BlockItem(INVISIBILITY_FOAM_TNT_REGISTRY, new Item.Properties().setId(INVISIBILITY_FOAM_TNT_ITEM_KEY)));
		Registry.register(BuiltInRegistries.BLOCK, id("invisibility_foam"), INVISIBILITY_FOAM_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("invisibility_foam"), INVISIBILITY_FOAM_ITEM_REGISTRY);

		// Register Poison Foam TNT & Block
		Registry.register(BuiltInRegistries.BLOCK, id("poison_foam_tnt"), POISON_FOAM_TNT_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("poison_foam_tnt"), new BlockItem(POISON_FOAM_TNT_REGISTRY, new Item.Properties().setId(POISON_FOAM_TNT_ITEM_KEY)));
		Registry.register(BuiltInRegistries.BLOCK, id("poison_foam"), POISON_FOAM_REGISTRY);
		Registry.register(BuiltInRegistries.ITEM, id("poison_foam"), POISON_FOAM_ITEM_REGISTRY);

		// Add items to Creative Menu
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
			content.accept(HEALING_FOAM_TNT_REGISTRY);
			content.accept(HEALING_FOAM_ITEM_REGISTRY);
			content.accept(INVISIBILITY_FOAM_TNT_REGISTRY);
			content.accept(INVISIBILITY_FOAM_ITEM_REGISTRY);
			content.accept(POISON_FOAM_TNT_REGISTRY);
			content.accept(POISON_FOAM_ITEM_REGISTRY);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
