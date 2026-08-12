package coolsquid.hungertweaker.ct.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.Loader;
import squeek.applecore.api.food.FoodValues;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import toughasnails.api.TANCapabilities;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.item.ItemDrink;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureScale;
import toughasnails.api.temperature.TemperatureScale.TemperatureRange;
import toughasnails.api.thirst.IDrink;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.api.thirst.WaterType;
import toughasnails.config.json.DrinkData;
import toughasnails.init.ModConfig;
import toughasnails.temperature.modifier.TemperatureModifier.ExternalModifier;

@ZenClass("mods.hungertweaker.ToughAsNails")
public class CTToughAsNails {

	public static final String MODID = "toughasnails";

	private CTToughAsNails() {
	}

	/**
	 * Registered after Forge has loaded TAN so CraftTweaker cannot scan this optional class too early.
	 */
	public static void registerIfLoaded() {
		if (isLoaded()) {
			CraftTweakerAPI.registerClass(CTToughAsNails.class);
			CraftTweakerAPI.registerClass(coolsquid.hungertweaker.ct.events.CTToughAsNailsDrinkEvent.class);
		}
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static boolean isThirstEnabled() {
		requireLoaded();
		return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST);
	}

	@ZenMethod
	public static boolean isTemperatureEnabled() {
		requireLoaded();
		return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE);
	}

	@ZenMethod
	public static boolean isPeacefulEnabled() {
		requireLoaded();
		return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_PEACEFUL);
	}

	@ZenMethod
	public static boolean isWorldDrinkingEnabled() {
		requireLoaded();
		return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST_WORLD);
	}

	@ZenMethod
	public static boolean isRainDrinkingEnabled() {
		requireLoaded();
		return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST_RAIN);
	}

	@ZenMethod
	public static int getThirst(IPlayer player) {
		return ((IThirst) getThirstCapability(player)).getThirst();
	}

	@ZenMethod
	public static void setThirst(IPlayer player, int thirst) {
		((IThirst) getThirstCapability(player)).setThirst(thirst);
	}

	@ZenMethod
	public static void addThirst(IPlayer player, int amount) {
		((IThirst) getThirstCapability(player)).setThirst(getThirst(player) + amount);
	}

	@ZenMethod
	public static float getHydration(IPlayer player) {
		return ((IThirst) getThirstCapability(player)).getHydration();
	}

	@ZenMethod
	public static void setHydration(IPlayer player, float hydration) {
		((IThirst) getThirstCapability(player)).setHydration(hydration);
	}

	@ZenMethod
	public static void addHydration(IPlayer player, float amount) {
		((IThirst) getThirstCapability(player)).setHydration(getHydration(player) + amount);
	}

	@ZenMethod
	public static float getThirstExhaustion(IPlayer player) {
		return ((IThirst) getThirstCapability(player)).getExhaustion();
	}

	@ZenMethod
	public static void setThirstExhaustion(IPlayer player, float exhaustion) {
		((IThirst) getThirstCapability(player)).setExhaustion(exhaustion);
	}

	@ZenMethod
	public static void addThirstExhaustion(IPlayer player, float amount) {
		((IThirst) getThirstCapability(player)).setExhaustion(getThirstExhaustion(player) + amount);
	}

	@ZenMethod
	public static void addThirstStats(IPlayer player, int thirst, float hydration) {
		((IThirst) getThirstCapability(player)).addStats(thirst, hydration);
	}

	@ZenMethod
	public static int getThirstChangeTime(IPlayer player) {
		return ((IThirst) getThirstCapability(player)).getChangeTime();
	}

	@ZenMethod
	public static void setThirstChangeTime(IPlayer player, int ticks) {
		((IThirst) getThirstCapability(player)).setChangeTime(ticks);
	}

	@ZenMethod
	public static boolean isThirsty(IPlayer player) {
		return ((IThirst) getThirstCapability(player)).getThirst() < 20;
	}

	@ZenMethod
	public static IData getThirstData(IPlayer player) {
		IThirst thirst = (IThirst) getThirstCapability(player);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("thirst", CTCompatData.integer(thirst.getThirst()));
		data.put("hydration", CTCompatData.floating(thirst.getHydration()));
		data.put("exhaustion", CTCompatData.floating(thirst.getExhaustion()));
		data.put("changeTime", CTCompatData.integer(thirst.getChangeTime()));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static int getTemperature(IPlayer player) {
		return ((ITemperature) getTemperatureCapability(player)).getTemperature().getRawValue();
	}

	@ZenMethod
	public static void setTemperature(IPlayer player, int temperature) {
		((ITemperature) getTemperatureCapability(player)).setTemperature(new Temperature(clampTemperature(temperature)));
	}

	@ZenMethod
	public static void addTemperature(IPlayer player, int amount) {
		((ITemperature) getTemperatureCapability(player)).addTemperature(new Temperature(amount));
	}

	@ZenMethod
	public static int getPlayerTargetTemperature(IPlayer player) {
		ITemperature temperature = (ITemperature) getTemperatureCapability(player);
		return temperature.getPlayerTarget(getPlayer(player));
	}

	@ZenMethod
	public static int getTemperatureChangeTime(IPlayer player) {
		return ((ITemperature) getTemperatureCapability(player)).getChangeTime();
	}

	@ZenMethod
	public static void setTemperatureChangeTime(IPlayer player, int ticks) {
		((ITemperature) getTemperatureCapability(player)).setChangeTime(ticks);
	}

	@ZenMethod
	public static void applyTemperatureModifier(IPlayer player, String name, int amount, int rate, int duration) {
		((ITemperature) getTemperatureCapability(player)).applyModifier(name, amount, rate, duration);
	}

	@ZenMethod
	public static boolean hasTemperatureModifier(IPlayer player, String name) {
		return ((ITemperature) getTemperatureCapability(player)).hasModifier(name);
	}

	@ZenMethod
	public static IData getTemperatureModifiers(IPlayer player) {
		Map<String, IData> data = new LinkedHashMap<>();
		for (Map.Entry<?, ?> entry : ((ITemperature) getTemperatureCapability(player)).getExternalModifiers().entrySet()) {
			data.put(String.valueOf(entry.getKey()), externalModifierData(entry.getValue()));
		}
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static IData getTemperatureData(IPlayer player) {
		ITemperature temperature = (ITemperature) getTemperatureCapability(player);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("temperature", CTCompatData.integer(temperature.getTemperature().getRawValue()));
		data.put("range", CTCompatData.string(temperatureRange(temperature.getTemperature().getRawValue())));
		data.put("targetTemperature", CTCompatData.integer(temperature.getPlayerTarget(getPlayer(player))));
		data.put("changeTime", CTCompatData.integer(temperature.getChangeTime()));
		data.put("modifiers", getTemperatureModifiers(player));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static int clampTemperature(int temperature) {
		requireLoaded();
		return Math.max(0, Math.min(TemperatureScale.getScaleTotal(), temperature));
	}

	@ZenMethod
	public static int getTemperatureScaleTotal() {
		requireLoaded();
		return TemperatureScale.getScaleTotal();
	}

	@ZenMethod
	public static int getTemperatureScaleMidpoint() {
		requireLoaded();
		return TemperatureScale.getScaleMidpoint();
	}

	@ZenMethod
	public static String[] getTemperatureRanges() {
		requireLoaded();
		TemperatureRange[] ranges = TemperatureRange.values();
		String[] names = new String[ranges.length];
		for (int i = 0; i < ranges.length; i++) {
			names[i] = ranges[i].name();
		}
		return names;
	}

	@ZenMethod
	public static String getTemperatureRange(int temperature) {
		requireLoaded();
		return temperatureRange(temperature);
	}

	@ZenMethod
	public static IData getTemperatureRangeInfo(String rangeName) {
		requireLoaded();
		TemperatureRange range = (TemperatureRange) getTemperatureRangeByName(rangeName);
		int lower = TemperatureScale.getRangeStart(range);
		int upper = lower + range.getRangeSize() - 1;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("name", CTCompatData.string(range.name()));
		data.put("lowerBound", CTCompatData.integer(lower));
		data.put("upperBound", CTCompatData.integer(upper));
		data.put("middle", CTCompatData.integer((lower + upper) / 2));
		data.put("size", CTCompatData.integer(range.getRangeSize()));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static String[] getWaterTypes() {
		requireLoaded();
		WaterType[] types = WaterType.values();
		String[] names = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			names[i] = types[i].name();
		}
		return names;
	}

	@ZenMethod
	public static IData getWaterTypeInfo(String typeName) {
		requireLoaded();
		return drinkData(WaterType.valueOf(normalizeEnumName(typeName)), "water");
	}

	@ZenMethod
	public static IData getDrinkData(IItemStack food) {
		requireLoaded();
		ItemStack stack = getStack(food);
		Object itemDrink = findItemDrink(stack);
		if (itemDrink != null) {
			return drinkData(itemDrink, "item");
		}
		Object configured = findConfiguredDrink(stack);
		if (configured != null) {
			return drinkData(configured, "config");
		}
		if (stack.getItem() == Items.POTIONITEM) {
			return drinkDataForPotion(stack);
		}
		return emptyDrinkData();
	}

	@ZenMethod
	public static IData getFoodData(IItemStack food) {
		return getDrinkData(food);
	}

	@ZenMethod
	public static void drink(IPlayer player, int thirst, float hydration) {
		((IThirst) getThirstCapability(player)).addStats(thirst, hydration);
	}

	public static boolean isSupportedDrink(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		return findItemDrink(stack) != null || findConfiguredDrink(stack) != null || stack.getItem() == Items.POTIONITEM;
	}

	private static IData drinkData(Object drink, String source) {
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("matched", CTCompatData.bool(true));
		data.put("source", CTCompatData.string(source));
		if (drink instanceof DrinkData) {
			DrinkData configured = (DrinkData) drink;
			data.put("thirst", CTCompatData.integer(configured.getThirstRestored()));
			data.put("hydration", CTCompatData.floating(configured.getHydrationRestored()));
			data.put("poisonChance", CTCompatData.floating(configured.getPoisonChance()));
		} else {
			IDrink internal = (IDrink) drink;
			data.put("thirst", CTCompatData.integer(internal.getThirst()));
			data.put("hydration", CTCompatData.floating(internal.getHydration()));
			data.put("poisonChance", CTCompatData.floating(internal.getPoisonChance()));
		}
		return CTCompatData.map(data);
	}

	private static IData drinkDataForPotion(ItemStack stack) {
		boolean water = PotionUtils.getFullEffectsFromItem(stack).isEmpty();
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("matched", CTCompatData.bool(true));
		data.put("source", CTCompatData.string("potion"));
		data.put("thirst", CTCompatData.integer(water ? WaterType.NORMAL.getThirst() : 4));
		data.put("hydration", CTCompatData.floating(water ? WaterType.NORMAL.getHydration() : 0.3F));
		data.put("poisonChance", CTCompatData.floating(water ? WaterType.NORMAL.getPoisonChance() : 0));
		return CTCompatData.map(data);
	}

	private static IData emptyDrinkData() {
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("matched", CTCompatData.bool(false));
		data.put("source", CTCompatData.string("none"));
		data.put("thirst", CTCompatData.integer(0));
		data.put("hydration", CTCompatData.floating(0));
		data.put("poisonChance", CTCompatData.floating(0));
		return CTCompatData.map(data);
	}

	private static Object findItemDrink(ItemStack stack) {
		if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemDrink)) {
			return null;
		}
		return ((ItemDrink) stack.getItem()).getTypeFromMeta(stack.getMetadata());
	}

	private static Object findConfiguredDrink(ItemStack stack) {
		if (stack == null || stack.isEmpty() || stack.getItem().getRegistryName() == null) {
			return null;
		}
		List<?> entries = ModConfig.drinkData.get(stack.getItem().getRegistryName().toString());
		if (entries == null) {
			return null;
		}
		for (Object entry : entries) {
			if (entry instanceof DrinkData && ((DrinkData) entry).getPredicate().apply(stack)) {
				return entry;
			}
		}
		return null;
	}

	private static IData externalModifierData(Object modifierObject) {
		ExternalModifier modifier = (ExternalModifier) modifierObject;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("name", CTCompatData.string(modifier.getName()));
		data.put("amount", CTCompatData.integer(modifier.getAmount()));
		data.put("rate", CTCompatData.integer(modifier.getRate()));
		data.put("endTime", CTCompatData.integer(modifier.getEndTime()));
		return CTCompatData.map(data);
	}

	private static String temperatureRange(int temperature) {
		TemperatureRange range = TemperatureScale.getTemperatureRange(temperature);
		return range == null ? "UNKNOWN" : range.name();
	}

	private static Object getTemperatureRangeByName(String name) {
		try {
			return TemperatureRange.valueOf(normalizeEnumName(name));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown Tough As Nails temperature range '" + name
					+ "'. Possible values: " + joinNames(TemperatureRange.values()) + ".", e);
		}
	}

	private static String joinNames(Object rangesObject) {
		TemperatureRange[] ranges = (TemperatureRange[]) rangesObject;
		StringBuilder builder = new StringBuilder();
		for (TemperatureRange range : ranges) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(range.name());
		}
		return builder.toString();
	}

	private static String normalizeEnumName(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Enum name cannot be null.");
		}
		return value.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
	}

	private static ItemStack getStack(IItemStack food) {
		return food == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(food);
	}

	private static EntityPlayer getPlayer(IPlayer player) {
		return CraftTweakerMC.getPlayer(player);
	}

	private static Object getThirstCapability(IPlayer player) {
		requireLoaded();
		IThirst thirst = ThirstHelper.getThirstData(getPlayer(player));
		if (thirst == null) {
			throw new IllegalStateException("Tough As Nails thirst capability is unavailable for this player.");
		}
		return thirst;
	}

	private static Object getTemperatureCapability(IPlayer player) {
		requireLoaded();
		ITemperature temperature = getPlayer(player).getCapability(TANCapabilities.TEMPERATURE, null);
		if (temperature == null) {
			throw new IllegalStateException("Tough As Nails temperature capability is unavailable for this player.");
		}
		return temperature;
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("Tough As Nails is not loaded.");
		}
	}
}
