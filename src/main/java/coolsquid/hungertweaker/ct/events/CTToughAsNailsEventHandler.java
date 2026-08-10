package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTToughAsNails;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTToughAsNailsEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDrink(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntityLiving() instanceof EntityPlayer
				&& !event.getEntityLiving().world.isRemote
				&& CTToughAsNails.isSupportedDrink(event.getItem())
				&& HungerEventManager.TOUGH_AS_NAILS_DRINK.hasHandlers()) {
			HungerEventManager.TOUGH_AS_NAILS_DRINK.publish(new CTToughAsNailsDrinkEvent(event));
		}
	}
}
