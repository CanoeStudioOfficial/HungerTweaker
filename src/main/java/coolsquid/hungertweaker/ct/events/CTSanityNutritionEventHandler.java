package coolsquid.hungertweaker.ct.events;

import java.util.List;
import java.util.Map;

import ca.wescook.nutrition.capabilities.INutrientManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import coolsquid.hungertweaker.ct.compat.CTSanity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Applies Sanity's Nutrition compatibility factors without loading Nutrition when it is absent. */
public class CTSanityNutritionEventHandler {

	@CapabilityInject(INutrientManager.class)
	private static final Capability<INutrientManager> NUTRITION_CAPABILITY = null;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (event.phase != TickEvent.Phase.END || player.world.isRemote || player.isCreative()
				|| player.isSpectator() || player.ticksExisted % 20 != 0) {
			return;
		}
		ISanity sanity = player.getCapability(Capabilities.SANITY, null);
		INutrientManager manager = player.getCapability(NUTRITION_CAPABILITY, null);
		if (sanity == null || manager == null) {
			return;
		}
		List<Nutrient> visible = NutrientList.getVisible();
		if (visible.isEmpty()) {
			return;
		}
		Map<Nutrient, Float> values = manager.get();
		float total = 0;
		int count = 0;
		for (Nutrient nutrient : visible) {
			Float value = values.get(nutrient);
			if (value != null) {
				total += value;
				count++;
			}
		}
		if (count == 0) {
			return;
		}
		float average = total / count;
		double[] factors = CTSanity.getNutritionFactors();
		if (average < factors[2] || average > factors[3]) {
			sanity.setIncreaseFactor(average < factors[2] ? factors[0] : factors[1]);
			sanity.setDecreaseFactor(average > factors[3] ? factors[1] : factors[0]);
		}
	}
}
