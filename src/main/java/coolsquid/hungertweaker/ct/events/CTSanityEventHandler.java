package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTSanity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.food.FoodEvent;

/** Event bridge for the optional Sanity API. Loaded only when Sanity is present. */
public class CTSanityEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void on(FoodEvent.FoodEaten event) {
		if (!(event.player instanceof EntityPlayer) || event.player.world.isRemote) {
			return;
		}
		CTSanity.applyFoodValue(event.player, event.food);
		if (HungerEventManager.SANITY_FOOD_EATEN.hasHandlers()) {
			HungerEventManager.SANITY_FOOD_EATEN.publish(new CTSanityFoodEatenEvent(event));
		}
	}
}
