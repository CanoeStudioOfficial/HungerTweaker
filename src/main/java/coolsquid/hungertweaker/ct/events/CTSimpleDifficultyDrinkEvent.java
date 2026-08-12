package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTSimpleDifficulty;
import crafttweaker.api.data.IData;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("mods.hungertweaker.events.SimpleDifficultyDrinkEvent")
public class CTSimpleDifficultyDrinkEvent extends CTDrinkEvent {

	public CTSimpleDifficultyDrinkEvent(LivingEntityUseItemEvent.Finish internal) {
		super(internal);
	}

	@ZenGetter("drink")
	public IData getDrink() {
		return CTSimpleDifficulty.getConsumableThirst(internalFood());
	}

	@ZenGetter("thirstLevel")
	public int getThirstLevel() {
		return CTSimpleDifficulty.getThirstLevel(getPlayer());
	}

	@ZenGetter("saturation")
	public float getSaturation() {
		return CTSimpleDifficulty.getThirstSaturation(getPlayer());
	}

	@ZenGetter("exhaustion")
	public float getExhaustion() {
		return CTSimpleDifficulty.getThirstExhaustion(getPlayer());
	}
}
