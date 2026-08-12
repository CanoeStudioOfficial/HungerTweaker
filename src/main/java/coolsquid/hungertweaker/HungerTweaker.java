package coolsquid.hungertweaker;

import coolsquid.hungertweaker.ct.compat.CTToughAsNails;
import coolsquid.hungertweaker.ct.compat.CTFoodSpoiling;
import coolsquid.hungertweaker.ct.compat.CTNutrition;
import coolsquid.hungertweaker.ct.compat.CTSanity;
import coolsquid.hungertweaker.ct.compat.CTSimpleDifficulty;
import coolsquid.hungertweaker.ct.compat.CTSpiceOfLife;
import coolsquid.hungertweaker.ct.compat.CTSpiceOfLifeCarrotEdition;
import coolsquid.hungertweaker.ct.CTHungerOverhaul;
import coolsquid.hungertweaker.ct.events.CTSimpleDifficultyEventHandler;
import coolsquid.hungertweaker.ct.events.CTSanityEventHandler;
import coolsquid.hungertweaker.ct.events.CTSanityFoodSpoilingEventHandler;
import coolsquid.hungertweaker.ct.events.CTSanityNutritionEventHandler;
import coolsquid.hungertweaker.ct.events.CTToughAsNailsEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.Loader;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
		dependencies = HungerTweaker.DEPENDENCIES)
public class HungerTweaker {

	public static final String MOD_ID = Tags.MOD_ID;
	public static final String NAME = Tags.MOD_NAME;
	public static final String VERSION = Tags.VERSION;
	public static final String DEPENDENCIES = "required-after:crafttweaker@[4.0.0,);required-after:applecore@[3.2.0,);"
			+ "after:nutrition;after:spiceoflife;after:solcarrot;after:foodspoiling;after:toughasnails;"
			+ "after:simpledifficulty;after:sanity";

	public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		// Must be done in pre-init, otherwise the getters in CTFoodValues won't work
		// properly when scripts are executed
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
		if (Loader.isModLoaded("toughasnails")) {
			try {
				CTToughAsNails.registerIfLoaded();
			} catch (LinkageError e) {
				LOGGER.warn("Tough As Nails CT integration was not registered because its API is incompatible.", e);
			}
			MinecraftForge.EVENT_BUS.register(new CTToughAsNailsEventHandler());
		}
		registerOptionalCompatibility("nutrition", new OptionalRegistration() {
			@Override
			public void register() {
				CTNutrition.registerIfLoaded();
			}
		});
		registerOptionalCompatibility("foodspoiling", new OptionalRegistration() {
			@Override
			public void register() {
				CTFoodSpoiling.registerIfLoaded();
			}
		});
		registerOptionalCompatibility("spiceoflife", new OptionalRegistration() {
			@Override
			public void register() {
				CTSpiceOfLife.registerIfLoaded();
			}
		});
		registerOptionalCompatibility("solcarrot", new OptionalRegistration() {
			@Override
			public void register() {
				CTSpiceOfLifeCarrotEdition.registerIfLoaded();
			}
		});
		registerOptionalCompatibility("simpledifficulty", new OptionalRegistration() {
			@Override
			public void register() {
				CTSimpleDifficulty.registerIfLoaded();
			}
		});
		if (Loader.isModLoaded("sanity")) {
			boolean sanityIntegrationAvailable = false;
			try {
				CTSanity.registerIfLoaded();
				MinecraftForge.EVENT_BUS.register(new CTSanityEventHandler());
				sanityIntegrationAvailable = true;
			} catch (LinkageError e) {
				LOGGER.warn("Sanity CT integration was not registered because its API is incompatible.", e);
			}
			if (sanityIntegrationAvailable && Loader.isModLoaded("foodspoiling")) {
				try {
					MinecraftForge.EVENT_BUS.register(new CTSanityFoodSpoilingEventHandler());
				} catch (LinkageError e) {
					LOGGER.warn("Sanity/FoodSpoiling integration was not registered because its API is incompatible.", e);
				}
			}
			if (sanityIntegrationAvailable && Loader.isModLoaded(CTNutrition.MODID)) {
				try {
					MinecraftForge.EVENT_BUS.register(new CTSanityNutritionEventHandler());
				} catch (LinkageError e) {
					LOGGER.warn("Sanity/Nutrition integration was not registered because its API is incompatible.", e);
				}
			}
		}
		if (Loader.isModLoaded("simpledifficulty")) {
			MinecraftForge.EVENT_BUS.register(new CTSimpleDifficultyEventHandler());
		}
	}

	private static void registerOptionalCompatibility(String modId, OptionalRegistration registration) {
		if (!Loader.isModLoaded(modId)) {
			return;
		}
		try {
			registration.register();
		} catch (LinkageError e) {
			LOGGER.warn("Optional CT integration for " + modId
					+ " was not registered because its API is incompatible.", e);
		}
	}

	private interface OptionalRegistration {
		void register();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		if (Loader.isModLoaded(CTNutrition.MODID)) {
			try {
				CTNutrition.applyQueuedFoodChanges();
			} catch (LinkageError e) {
				LOGGER.warn("Nutrition food registration could not be applied because its API is incompatible.", e);
			}
		}
		CTHungerOverhaul.applyFoodStackSizes();
	}

}
