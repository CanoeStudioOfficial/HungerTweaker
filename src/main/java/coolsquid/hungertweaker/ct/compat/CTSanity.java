package coolsquid.hungertweaker.ct.compat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.origins_eternity.sanity.capability.Capabilities;
import com.origins_eternity.sanity.capability.sanity.ISanity;
import com.origins_eternity.sanity.config.Configuration;
import coolsquid.hungertweaker.ct.events.CTSanityFoodEatenEvent;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** Optional Sanity integration registered only when Sanity is installed. */
@ZenClass("mods.hungertweaker.Sanity")
public class CTSanity {

	public static final String MODID = "sanity";
	private static final List<FoodRule> FOOD_RULES = new ArrayList<>();
	private static double defaultFoodValue;
	private static boolean defaultFoodValueConfigured;
	private static double spoiledFoodPenalty;
	private static boolean spoiledFoodPenaltyConfigured;
	private static double nutritionDecreaseFactor;
	private static double nutritionIncreaseFactor;
	private static float nutritionMinimum = 20;
	private static float nutritionMaximum = 80;
	private static boolean nutritionFactorsConfigured;

	private CTSanity() {
	}

	/**
	 * Sanity is optional, so this class must only be registered after Forge has
	 * confirmed that Sanity is present.
	 */
	public static void registerIfLoaded() {
		if (isLoaded()) {
			CraftTweakerAPI.registerClass(CTSanity.class);
			CraftTweakerAPI.registerClass(CTSanityFoodEatenEvent.class);
		}
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static float getSanity(IPlayer player) {
		return getSanityCapability(player).getSanity();
	}

	@ZenMethod
	public static float getMaxSanity(IPlayer player) {
		return getSanityCapability(player).getMax();
	}

	@ZenMethod
	public static void setSanity(IPlayer player, double value) {
		getSanityCapability(player).setSanity(value);
	}

	@ZenMethod
	public static void addSanity(IPlayer player, double amount) {
		if (amount >= 0) {
			recoverSanity(player, amount);
		} else {
			consumeSanity(player, -amount);
		}
	}

	@ZenMethod
	public static void recoverSanity(IPlayer player, double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Sanity recovery cannot be negative.");
		}
		getSanityCapability(player).recoverSanity(amount);
	}

	@ZenMethod
	public static void consumeSanity(IPlayer player, double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Sanity consumption cannot be negative.");
		}
		getSanityCapability(player).consumeSanity(amount);
	}

	@ZenMethod
	public static boolean isEnabled(IPlayer player) {
		return getSanityCapability(player).getEnable();
	}

	/** Sets the extra Sanity value applied to foods without a matching rule. */
	@ZenMethod
	public static void setDefaultFoodValue(double value) {
		defaultFoodValue = value;
		defaultFoodValueConfigured = true;
	}

	@ZenMethod
	public static double getDefaultFoodValue() {
		return defaultFoodValue;
	}

	/** Sets the Sanity lost when a fully spoiled food is eaten. */
	@ZenMethod
	public static void setSpoiledFoodPenalty(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Spoiled food Sanity penalty cannot be negative.");
		}
		spoiledFoodPenalty = amount;
		spoiledFoodPenaltyConfigured = true;
	}

	@ZenMethod
	public static double getSpoiledFoodPenalty() {
		return spoiledFoodPenaltyConfigured ? spoiledFoodPenalty : 0;
	}

	/** Overrides Sanity's Nutrition compatibility factors and average range. */
	@ZenMethod
	public static void setNutritionFactors(double decreaseFactor, double increaseFactor,
			float minimum, float maximum) {
		if (decreaseFactor < 0 || increaseFactor < 0 || minimum > maximum) {
			throw new IllegalArgumentException("Invalid Sanity Nutrition factors or range.");
		}
		nutritionDecreaseFactor = decreaseFactor;
		nutritionIncreaseFactor = increaseFactor;
		nutritionMinimum = minimum;
		nutritionMaximum = maximum;
		nutritionFactorsConfigured = true;
	}

	public static double[] getNutritionFactors() {
		if (nutritionFactorsConfigured) {
			return new double[] { nutritionDecreaseFactor, nutritionIncreaseFactor,
					nutritionMinimum, nutritionMaximum };
		}
		String[] parts = Configuration.Compat.nutrition.split(";");
		if (parts.length != 4) {
			return new double[] { 0.8, 1.25, 20, 80 };
		}
		try {
			return new double[] { Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
					Double.parseDouble(parts[2]), Double.parseDouble(parts[3]) };
		} catch (NumberFormatException ignored) {
			return new double[] { 0.8, 1.25, 20, 80 };
		}
	}

	public static double getConfiguredFoodSpoilingPenalty() {
		return spoiledFoodPenaltyConfigured ? spoiledFoodPenalty : Configuration.Compat.foodSpoiling;
	}

	@ZenMethod
	public static void setFoodValue(IIngredient food, double value) {
		clearFoodValue(food);
		addFoodValue(food, value);
	}

	/** Adds an extra Sanity value for every matching food. */
	@ZenMethod
	public static void addFoodValue(IIngredient food, double value) {
		FOOD_RULES.add(new FoodRule(food, value));
	}

	@ZenMethod
	public static void clearFoodValue(IIngredient food) {
		Iterator<FoodRule> iterator = FOOD_RULES.iterator();
		while (iterator.hasNext()) {
			IIngredient configured = iterator.next().food;
			if (configured == food || configured.toCommandString().equals(food.toCommandString())) {
				iterator.remove();
			}
		}
	}

	@ZenMethod
	public static void clearFoodValues() {
		FOOD_RULES.clear();
		defaultFoodValue = 0;
		defaultFoodValueConfigured = false;
	}

	@ZenMethod
	public static double getFoodValue(IItemStack food) {
		return foodValue(food);
	}

	public static boolean hasConfiguredFoodValue(ItemStack food) {
		if (defaultFoodValueConfigured) {
			return true;
		}
		IItemStack stack = CraftTweakerMC.getIItemStack(food);
		for (FoodRule rule : FOOD_RULES) {
			if (rule.food.matches(stack)) {
				return true;
			}
		}
		return false;
	}

	public static void applyFoodValue(EntityPlayer player, ItemStack food) {
		if (!isLoaded() || player == null || food == null || food.isEmpty() || player.world.isRemote
				|| player.capabilities.isCreativeMode || !hasConfiguredFoodValue(food)) {
			return;
		}
		double value = foodValue(CraftTweakerMC.getIItemStack(food));
		if (value > 0) {
			if (CTFoodSpoiling.isLoaded()) {
				float freshness = CTFoodSpoiling.getFreshness(CraftTweakerMC.getIPlayer(player),
						CraftTweakerMC.getIItemStack(food));
				if (freshness <= 0 && getConfiguredFoodSpoilingPenalty() > 0) {
					consumeSanity(CraftTweakerMC.getIPlayer(player), getConfiguredFoodSpoilingPenalty());
					return;
				}
				value *= freshness;
			}
			recoverSanity(CraftTweakerMC.getIPlayer(player), value);
		} else if (value < 0) {
			consumeSanity(CraftTweakerMC.getIPlayer(player), -value);
		}
	}

	private static double foodValue(IItemStack food) {
		double value = defaultFoodValue;
		for (FoodRule rule : FOOD_RULES) {
			if (rule.food.matches(food)) {
				value += rule.value;
			}
		}
		return value;
	}

	private static ISanity getSanityCapability(IPlayer player) {
		requireLoaded();
		ISanity sanity = CraftTweakerMC.getPlayer(player).getCapability(Capabilities.SANITY, null);
		if (sanity == null) {
			throw new IllegalStateException("Sanity capability is unavailable for this player.");
		}
		return sanity;
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("Sanity is not loaded.");
		}
	}

	private static class FoodRule {
		private final IIngredient food;
		private final double value;

		private FoodRule(IIngredient food, double value) {
			this.food = food;
			this.value = value;
		}
	}
}
