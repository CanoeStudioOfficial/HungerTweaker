package coolsquid.hungertweaker.ct.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.wescook.nutrition.api.INutrient;
import ca.wescook.nutrition.api.INutrientItemEntry;
import ca.wescook.nutrition.api.ItemStackCompareType;
import ca.wescook.nutrition.api.NutritionUtil;
import ca.wescook.nutrition.network.Sync;
import ca.wescook.nutrition.nutrients.Nutrient;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.hungertweaker.Nutrition")
public class CTNutrition {

	public static final String MODID = "nutrition";

	private static final String DAIRY = "dairy";
	private static final String FRUIT = "fruit";
	private static final String GRAIN = "grain";
	private static final String PROTEIN = "protein";
	private static final String VEGETABLE = "vegetable";
	private static final List<PendingFoodChange> PENDING_FOOD_CHANGES = new ArrayList<>();

	private CTNutrition() {
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static String[] getNutrientNames() {
		requireLoaded();
		List<String> names = new ArrayList<>();
		for (INutrient nutrient : NutritionUtil.getNutrients()) {
			names.add(nutrient.getName());
		}
		return names.toArray(new String[0]);
	}

	@ZenMethod
	public static IData getNutrients() {
		requireLoaded();
		Map<String, IData> data = new LinkedHashMap<>();
		for (INutrient nutrient : NutritionUtil.getNutrients()) {
			data.put(nutrient.getName(), nutrientData(nutrient));
		}
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static IData getNutrientInfo(String nutrientName) {
		requireLoaded();
		return nutrientData(getNutrient(nutrientName));
	}

	@ZenMethod
	public static IItemStack getNutrientIcon(String nutrientName) {
		requireLoaded();
		return CraftTweakerMC.getIItemStack(getNutrient(nutrientName).getIcon());
	}

	@ZenMethod
	public static int getNutrientColor(String nutrientName) {
		requireLoaded();
		return getNutrient(nutrientName).getColor();
	}

	@ZenMethod
	public static boolean isNutrientVisible(String nutrientName) {
		requireLoaded();
		INutrient nutrient = getNutrient(nutrientName);
		return nutrient instanceof Nutrient && ((Nutrient) nutrient).visible;
	}

	@ZenMethod
	public static float getNutrientDecay(String nutrientName) {
		requireLoaded();
		INutrient nutrient = getNutrient(nutrientName);
		return nutrient instanceof Nutrient ? ((Nutrient) nutrient).decay : 0;
	}

	@ZenMethod
	public static float getNutrient(IPlayer player, String nutrientName) {
		requireLoaded();
		return NutritionUtil.getNutrient(getPlayer(player), getNutrient(nutrientName));
	}

	@ZenMethod
	public static void setNutrient(IPlayer player, String nutrientName, float value) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		NutritionUtil.setNutrient(mcPlayer, getNutrient(nutrientName), value);
		sync(mcPlayer);
	}

	@ZenMethod
	public static void addNutrient(IPlayer player, String nutrientName, float amount) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		NutritionUtil.addNutrient(mcPlayer, getNutrient(nutrientName), amount);
		sync(mcPlayer);
	}

	@ZenMethod
	public static void resetNutrient(IPlayer player, String nutrientName) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		NutritionUtil.resetNutrient(mcPlayer, getNutrient(nutrientName));
		sync(mcPlayer);
	}

	@ZenMethod
	public static IData getPlayerNutrition(IPlayer player) {
		requireLoaded();
		return playerNutritionData(getPlayer(player));
	}

	@ZenMethod
	public static void setPlayerNutrition(IPlayer player, IData nutrients) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		for (Map.Entry<String, IData> entry : nutrients.asMap().entrySet()) {
			NutritionUtil.setNutrient(mcPlayer, getNutrient(entry.getKey()), entry.getValue().asFloat());
		}
		sync(mcPlayer);
	}

	@ZenMethod
	public static void addPlayerNutrition(IPlayer player, IData nutrients) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		for (Map.Entry<String, IData> entry : nutrients.asMap().entrySet()) {
			NutritionUtil.addNutrient(mcPlayer, getNutrient(entry.getKey()), entry.getValue().asFloat());
		}
		sync(mcPlayer);
	}

	@ZenMethod
	public static void resetPlayerNutrition(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		for (INutrient nutrient : NutritionUtil.getNutrients()) {
			NutritionUtil.resetNutrient(mcPlayer, nutrient);
		}
		sync(mcPlayer);
	}

	@ZenMethod
	public static float getDairy(IPlayer player) {
		return getNutrient(player, DAIRY);
	}

	@ZenMethod
	public static void setDairy(IPlayer player, float value) {
		setNutrient(player, DAIRY, value);
	}

	@ZenMethod
	public static void addDairy(IPlayer player, float amount) {
		addNutrient(player, DAIRY, amount);
	}

	@ZenMethod
	public static void resetDairy(IPlayer player) {
		resetNutrient(player, DAIRY);
	}

	@ZenMethod
	public static float getFruit(IPlayer player) {
		return getNutrient(player, FRUIT);
	}

	@ZenMethod
	public static void setFruit(IPlayer player, float value) {
		setNutrient(player, FRUIT, value);
	}

	@ZenMethod
	public static void addFruit(IPlayer player, float amount) {
		addNutrient(player, FRUIT, amount);
	}

	@ZenMethod
	public static void resetFruit(IPlayer player) {
		resetNutrient(player, FRUIT);
	}

	@ZenMethod
	public static float getGrain(IPlayer player) {
		return getNutrient(player, GRAIN);
	}

	@ZenMethod
	public static void setGrain(IPlayer player, float value) {
		setNutrient(player, GRAIN, value);
	}

	@ZenMethod
	public static void addGrain(IPlayer player, float amount) {
		addNutrient(player, GRAIN, amount);
	}

	@ZenMethod
	public static void resetGrain(IPlayer player) {
		resetNutrient(player, GRAIN);
	}

	@ZenMethod
	public static float getProtein(IPlayer player) {
		return getNutrient(player, PROTEIN);
	}

	@ZenMethod
	public static void setProtein(IPlayer player, float value) {
		setNutrient(player, PROTEIN, value);
	}

	@ZenMethod
	public static void addProtein(IPlayer player, float amount) {
		addNutrient(player, PROTEIN, amount);
	}

	@ZenMethod
	public static void resetProtein(IPlayer player) {
		resetNutrient(player, PROTEIN);
	}

	@ZenMethod
	public static float getVegetable(IPlayer player) {
		return getNutrient(player, VEGETABLE);
	}

	@ZenMethod
	public static void setVegetable(IPlayer player, float value) {
		setNutrient(player, VEGETABLE, value);
	}

	@ZenMethod
	public static void addVegetable(IPlayer player, float amount) {
		addNutrient(player, VEGETABLE, amount);
	}

	@ZenMethod
	public static void resetVegetable(IPlayer player) {
		resetNutrient(player, VEGETABLE);
	}

	@ZenMethod
	public static boolean isValidFood(IItemStack food) {
		requireLoaded();
		return NutritionUtil.isValidFood(getStack(food));
	}

	@ZenMethod
	public static boolean addFoodNutrition(IPlayer player, IItemStack food) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		boolean added = NutritionUtil.addNutrientsToPlayer(mcPlayer, getStack(food));
		if (added) {
			sync(mcPlayer);
		}
		return added;
	}

	@ZenMethod
	public static IData calculateFoodNutrition(IItemStack food) {
		requireLoaded();
		return foodNutritionData(getStack(food), null);
	}

	@ZenMethod
	public static IData calculateFoodNutritionForPlayer(IItemStack food, IPlayer player) {
		requireLoaded();
		return foodNutritionData(getStack(food), getPlayer(player));
	}

	@ZenMethod
	public static boolean foodContainsNutrient(IItemStack food, String nutrientName) {
		requireLoaded();
		return getNutrient(nutrientName).isContainedIn(getStack(food));
	}

	@ZenMethod
	public static float getFoodNutrientScale(IItemStack food, String nutrientName) {
		requireLoaded();
		INutrient nutrient = getNutrient(nutrientName);
		if (nutrient instanceof Nutrient) {
			Float scale = ((Nutrient) nutrient).getNutrientScale(getStack(food));
			return scale == null ? 0 : scale;
		}
		return nutrient.isContainedIn(getStack(food)) ? 1 : 0;
	}

	@ZenMethod
	public static void registerFoodItem(String nutrientName, IItemStack food) {
		registerFoodItem(nutrientName, food, 1, "META_SENSITIVE");
	}

	@ZenMethod
	public static void registerFoodItem(String nutrientName, IItemStack food, float scale) {
		registerFoodItem(nutrientName, food, scale, "META_SENSITIVE");
	}

	@ZenMethod
	public static void registerFoodItem(String nutrientName, IItemStack food, float scale,
			@Optional String compareType) {
		requireLoaded();
		PendingFoodChange change = PendingFoodChange.register(nutrientName, getStack(food), scale,
				compareType == null ? "META_SENSITIVE" : compareType);
		applyOrQueue(change);
	}

	@ZenMethod
	public static void removeFoodItem(String nutrientName, IItemStack food) {
		requireLoaded();
		applyOrQueue(PendingFoodChange.remove(nutrientName, getStack(food)));
	}

	public static IData playerNutritionData(EntityPlayer player) {
		requireLoaded();
		Map<String, IData> data = new LinkedHashMap<>();
		for (INutrient nutrient : NutritionUtil.getNutrients()) {
			data.put(nutrient.getName(), CTCompatData.floating(NutritionUtil.getNutrient(player, nutrient)));
		}
		return CTCompatData.map(data);
	}

	public static IData foodNutritionData(ItemStack food, EntityPlayer player) {
		requireLoaded();
		return nutritionMapData(NutritionUtil.calculateNutrition(food, player));
	}

	public static void applyQueuedFoodChanges() {
		if (!isLoaded() || PENDING_FOOD_CHANGES.isEmpty()) {
			return;
		}
		List<PendingFoodChange> queued = new ArrayList<>(PENDING_FOOD_CHANGES);
		PENDING_FOOD_CHANGES.clear();
		for (PendingFoodChange change : queued) {
			change.apply();
		}
	}

	private static IData nutritionMapData(Map<? extends INutrient, Float> nutrients) {
		Map<String, IData> data = new LinkedHashMap<>();
		for (Map.Entry<? extends INutrient, Float> entry : nutrients.entrySet()) {
			data.put(entry.getKey().getName(), CTCompatData.floating(entry.getValue()));
		}
		return CTCompatData.map(data);
	}

	private static IData nutrientData(INutrient nutrient) {
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("name", CTCompatData.string(nutrient.getName()));
		data.put("color", CTCompatData.integer(nutrient.getColor()));
		data.put("icon", CTCompatData.itemStack(nutrient.getIcon()));
		if (nutrient instanceof Nutrient) {
			Nutrient internal = (Nutrient) nutrient;
			data.put("decay", CTCompatData.floating(internal.decay));
			data.put("visible", CTCompatData.bool(internal.visible));
			data.put("oreDict", CTCompatData.strings(internal.foodOreDict));
			data.put("foodItemCount", CTCompatData.integer(internal.foodItems.size()));
		}
		return CTCompatData.map(data);
	}

	private static void applyOrQueue(PendingFoodChange change) {
		if (canResolveNutrient(change.nutrientName)) {
			change.apply();
		} else {
			PENDING_FOOD_CHANGES.add(change);
		}
	}

	private static boolean canResolveNutrient(String nutrientName) {
		return NutritionUtil.getNutrientByName(normalizeNutrientName(nutrientName)) != null;
	}

	private static INutrient getNutrient(String nutrientName) {
		INutrient nutrient = NutritionUtil.getNutrientByName(normalizeNutrientName(nutrientName));
		if (nutrient != null) {
			return nutrient;
		}
		throw new IllegalArgumentException("Unknown Nutrition nutrient '" + nutrientName + "'. Available nutrients: "
				+ joinNames(NutritionUtil.getNutrients()) + ".");
	}

	private static String joinNames(Collection<? extends INutrient> nutrients) {
		StringBuilder builder = new StringBuilder();
		for (INutrient nutrient : nutrients) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(nutrient.getName());
		}
		return builder.length() == 0 ? "<none loaded yet>" : builder.toString();
	}

	private static String normalizeNutrientName(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

	private static ItemStack getStack(IItemStack food) {
		return CraftTweakerMC.getItemStack(food);
	}

	private static EntityPlayer getPlayer(IPlayer player) {
		return CraftTweakerMC.getPlayer(player);
	}

	private static void sync(EntityPlayer player) {
		if (!player.world.isRemote && player instanceof EntityPlayerMP) {
			Sync.serverRequest(player);
		}
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("Nutrition is not loaded.");
		}
	}

	private static ItemStackCompareType parseCompareType(String compareType) {
		try {
			return ItemStackCompareType.valueOf(compareType.toUpperCase(Locale.ROOT).replace('-', '_'));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown Nutrition compare type '" + compareType
					+ "'. Possible values: DEFAULT, META_SENSITIVE, ONLY_NBT_SENSITIVE, ALL_SENSITIVE.", e);
		}
	}

	private static class PendingFoodChange {

		private final boolean remove;
		private final String nutrientName;
		private final ItemStack food;
		private final float scale;
		private final String compareType;

		private PendingFoodChange(boolean remove, String nutrientName, ItemStack food, float scale,
				String compareType) {
			this.remove = remove;
			this.nutrientName = nutrientName;
			this.food = food.copy();
			this.scale = scale;
			this.compareType = compareType;
		}

		static PendingFoodChange register(String nutrientName, ItemStack food, float scale, String compareType) {
			return new PendingFoodChange(false, nutrientName, food, scale, compareType);
		}

		static PendingFoodChange remove(String nutrientName, ItemStack food) {
			return new PendingFoodChange(true, nutrientName, food, 0, "META_SENSITIVE");
		}

		void apply() {
			INutrient nutrient = getNutrient(this.nutrientName);
			if (this.remove) {
				if (nutrient instanceof Nutrient) {
					((Nutrient) nutrient).removeScaledItemStack(this.food);
				}
				return;
			}
			INutrientItemEntry entry = INutrientItemEntry.create(this.food)
					.setScale(this.scale)
					.setCompareType(parseCompareType(this.compareType));
			nutrient.registerFoodItem(entry);
		}
	}
}
