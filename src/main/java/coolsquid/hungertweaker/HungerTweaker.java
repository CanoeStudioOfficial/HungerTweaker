package coolsquid.hungertweaker;

import coolsquid.hungertweaker.ct.compat.CTNutrition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
		dependencies = HungerTweaker.DEPENDENCIES)
public class HungerTweaker {

	public static final String MOD_ID = Tags.MOD_ID;
	public static final String NAME = Tags.MOD_NAME;
	public static final String VERSION = Tags.VERSION;
	public static final String DEPENDENCIES = "required-after:crafttweaker@[4.0.0,);required-after:applecore@[3.2.0,);"
			+ "after:nutrition;after:spiceoflife;after:solcarrot;after:foodspoiling";

	public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		// Must be done in pre-init, otherwise the getters in CTFoodValues won't work
		// properly when scripts are executed
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		CTNutrition.applyQueuedFoodChanges();
	}
}
