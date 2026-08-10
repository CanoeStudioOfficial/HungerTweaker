package coolsquid.hungertweaker;

import coolsquid.hungertweaker.ct.compat.CTNutrition;
import coolsquid.hungertweaker.ct.events.CTSimpleDifficultyEventHandler;
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
			+ "after:simpledifficulty";

	public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		// Must be done in pre-init, otherwise the getters in CTFoodValues won't work
		// properly when scripts are executed
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
		if (Loader.isModLoaded("toughasnails")) {
			MinecraftForge.EVENT_BUS.register(new CTToughAsNailsEventHandler());
		}
		if (Loader.isModLoaded("simpledifficulty")) {
			MinecraftForge.EVENT_BUS.register(new CTSimpleDifficultyEventHandler());
		}
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		if (Loader.isModLoaded(CTNutrition.MODID)) {
			CTNutrition.applyQueuedFoodChanges();
		}
	}
}
