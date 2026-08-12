package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTFoodSpoiling;
import squeek.applecore.api.food.FoodEvent.GetPlayerFoodValues;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.hungertweaker.events.FoodSpoilingFoodValuesEvent")
public class CTFoodSpoilingFoodValuesEvent extends CTGetFoodValuesEvent {

	private final GetPlayerFoodValues internal;

	public CTFoodSpoilingFoodValuesEvent(GetPlayerFoodValues internal) {
		super(internal);
		this.internal = internal;
	}

	@ZenGetter("rotState")
	public String getRotState() {
		return CTFoodSpoiling.getRotState(this.internal.player, this.internal.food);
	}

	@ZenGetter("canSpoil")
	public boolean canSpoil() {
		return CTFoodSpoiling.canSpoil(this.internal.player, this.internal.food);
	}

	@ZenGetter("saturationMultiplier")
	public float getSaturationMultiplier() {
		return CTFoodSpoiling.getSaturationMultiplier(this.internal.player, this.internal.food);
	}

	@ZenGetter("ticksToRot")
	public int getTicksToRot() {
		return CTFoodSpoiling.getTicksToRot(this.internal.player, this.internal.food);
	}

	@ZenGetter("remainingTicks")
	public int getRemainingTicks() {
		return CTFoodSpoiling.getRemainingTicks(this.internal.player, this.internal.food);
	}

	@ZenMethod
	public void applySpoilageToSaturation() {
		this.setSaturationModifier(this.getSaturationModifier() * this.getSaturationMultiplier());
	}

	@ZenMethod
	public void multiplySaturation(float multiplier) {
		this.setSaturationModifier(this.getSaturationModifier() * multiplier);
	}
}
