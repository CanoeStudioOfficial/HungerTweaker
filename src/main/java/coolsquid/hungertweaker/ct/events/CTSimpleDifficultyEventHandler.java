package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTSimpleDifficulty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CTSimpleDifficultyEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDrink(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntityLiving() instanceof EntityPlayer
				&& !event.getEntityLiving().world.isRemote
				&& CTSimpleDifficulty.isSupportedDrink(event.getItem())
				&& HungerEventManager.SIMPLE_DIFFICULTY_DRINK.hasHandlers()) {
			HungerEventManager.SIMPLE_DIFFICULTY_DRINK.publish(new CTSimpleDifficultyDrinkEvent(event));
		}
	}
}
