package coolsquid.hungertweaker.ct.events;

import coolsquid.hungertweaker.ct.compat.CTFoodSpoiling;
import coolsquid.hungertweaker.ct.compat.CTSanity;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import squeek.applecore.api.food.FoodEvent.FoodEaten;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.hungertweaker.events.SanityFoodEatenEvent")
public class CTSanityFoodEatenEvent extends CTFoodEatenEvent {

	private final FoodEaten internal;

	public CTSanityFoodEatenEvent(FoodEaten internal) {
		super(internal);
		this.internal = internal;
	}

	@ZenGetter("foodValue")
	public double getFoodValue() {
		return CTSanity.getFoodValue(CraftTweakerMC.getIItemStack(this.internal.food));
	}

	@ZenGetter("sanity")
	public float getSanity() {
		return CTSanity.getSanity(this.getPlayer());
	}

	@ZenGetter("freshness")
	public float getFreshness() {
		return CTFoodSpoiling.isLoaded()
				? CTFoodSpoiling.getFreshness(this.getPlayer(), CraftTweakerMC.getIItemStack(this.internal.food))
				: 1;
	}

	@ZenGetter("food")
	public IItemStack getSanityFood() {
		return CraftTweakerMC.getIItemStack(this.internal.food);
	}

	@ZenMethod
	public void setSanity(double value) {
		CTSanity.setSanity(this.getPlayer(), value);
	}

	@ZenMethod
	public void addSanity(double amount) {
		CTSanity.addSanity(this.getPlayer(), amount);
	}

	@ZenMethod
	public void recoverSanity(double amount) {
		CTSanity.recoverSanity(this.getPlayer(), amount);
	}

	@ZenMethod
	public void consumeSanity(double amount) {
		CTSanity.consumeSanity(this.getPlayer(), amount);
	}

	/** Applies the configured CT food value and scales positive recovery by freshness. */
	@ZenMethod
	public void applyFoodValue() {
		CTSanity.applyFoodValue(this.internal.player, this.internal.food);
	}
}
