package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTToughAsNails;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("mods.hungertweaker.events.ToughAsNailsDrinkEvent")
public class CTToughAsNailsDrinkEvent extends CTDrinkEvent {

	public CTToughAsNailsDrinkEvent(LivingEntityUseItemEvent.Finish internal) {
		super(internal);
	}

	@ZenGetter("drink")
	public IData getDrink() {
		return CTToughAsNails.getDrinkData(internalFood());
	}

	@ZenGetter("thirst")
	public int getThirst() {
		return CTToughAsNails.getThirst(getPlayer());
	}

	@ZenGetter("hydration")
	public float getHydration() {
		return CTToughAsNails.getHydration(getPlayer());
	}

	@ZenGetter("exhaustion")
	public float getExhaustion() {
		return CTToughAsNails.getThirstExhaustion(getPlayer());
	}
}
