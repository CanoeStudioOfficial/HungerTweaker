package coolsquid.hungertweaker.ct.events;

import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import coolsquid.hungertweaker.ct.compat.CTFoodSpoiling;
import coolsquid.hungertweaker.ct.compat.CTSanity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Applies the Sanity/FoodSpoiling inventory penalty from Sanity: Prequel. */
public class CTSanityFoodSpoilingEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (event.phase != TickEvent.Phase.END || player.world.isRemote || player.isCreative()
				|| player.isSpectator() || player.ticksExisted % 20 != 0) {
			return;
		}
		ISanity sanity = player.getCapability(Capabilities.SANITY, null);
		if (sanity == null) {
			return;
		}
		double penalty = CTSanity.getConfiguredFoodSpoilingPenalty();
		if (penalty <= 0) {
			return;
		}
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack.getItem() instanceof ItemFood
					&& CTFoodSpoiling.getFreshness(CraftTweakerMC.getIPlayer(player),
							CraftTweakerMC.getIItemStack(stack)) <= 0) {
				sanity.consumeSanity(penalty);
			}
		}
	}
}
