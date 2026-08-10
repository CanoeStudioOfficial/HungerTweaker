package coolsquid.hungertweaker.ct.compat;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.config.json.JsonItemIdentity;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyValue;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.api.temperature.TemporaryModifier;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.item.ItemDrinkBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.hungertweaker.SimpleDifficulty")
public class CTSimpleDifficulty {

	public static final String MODID = "simpledifficulty";

	private CTSimpleDifficulty() {
	}

	@ZenMethod
	public static boolean isLoaded() {
		return Loader.isModLoaded(MODID);
	}

	@ZenMethod
	public static boolean isThirstEnabled() {
		requireLoaded();
		return QuickConfig.isThirstEnabled();
	}

	@ZenMethod
	public static boolean isTemperatureEnabled() {
		requireLoaded();
		return QuickConfig.isTemperatureEnabled();
	}

	@ZenMethod
	public static int getThirstLevel(IPlayer player) {
		return ((IThirstCapability) thirstCapability(player)).getThirstLevel();
	}

	@ZenMethod
	public static void setThirstLevel(IPlayer player, int thirst) {
		((IThirstCapability) thirstCapability(player)).setThirstLevel(thirst);
	}

	@ZenMethod
	public static void addThirstLevel(IPlayer player, int amount) {
		((IThirstCapability) thirstCapability(player)).addThirstLevel(amount);
	}

	@ZenMethod
	public static float getThirstSaturation(IPlayer player) {
		return ((IThirstCapability) thirstCapability(player)).getThirstSaturation();
	}

	@ZenMethod
	public static void setThirstSaturation(IPlayer player, float saturation) {
		((IThirstCapability) thirstCapability(player)).setThirstSaturation(saturation);
	}

	@ZenMethod
	public static void addThirstSaturation(IPlayer player, float amount) {
		((IThirstCapability) thirstCapability(player)).addThirstSaturation(amount);
	}

	@ZenMethod
	public static float getThirstExhaustion(IPlayer player) {
		return ((IThirstCapability) thirstCapability(player)).getThirstExhaustion();
	}

	@ZenMethod
	public static void setThirstExhaustion(IPlayer player, float exhaustion) {
		((IThirstCapability) thirstCapability(player)).setThirstExhaustion(exhaustion);
	}

	@ZenMethod
	public static void addThirstExhaustion(IPlayer player, float amount) {
		((IThirstCapability) thirstCapability(player)).addThirstExhaustion(amount);
	}

	@ZenMethod
	public static int getThirstTickTimer(IPlayer player) {
		return ((IThirstCapability) thirstCapability(player)).getThirstTickTimer();
	}

	@ZenMethod
	public static void setThirstTickTimer(IPlayer player, int ticks) {
		((IThirstCapability) thirstCapability(player)).setThirstTickTimer(ticks);
	}

	@ZenMethod
	public static void addThirstTickTimer(IPlayer player, int ticks) {
		((IThirstCapability) thirstCapability(player)).addThirstTickTimer(ticks);
	}

	@ZenMethod
	public static int getThirstDamageCounter(IPlayer player) {
		return ((IThirstCapability) thirstCapability(player)).getThirstDamageCounter();
	}

	@ZenMethod
	public static void setThirstDamageCounter(IPlayer player, int value) {
		((IThirstCapability) thirstCapability(player)).setThirstDamageCounter(value);
	}

	@ZenMethod
	public static void addThirstDamageCounter(IPlayer player, int value) {
		((IThirstCapability) thirstCapability(player)).addThirstDamageCounter(value);
	}

	@ZenMethod
	public static boolean isThirsty(IPlayer player) {
		return ((IThirstCapability) thirstCapability(player)).isThirsty();
	}

	@ZenMethod
	public static IData getThirstData(IPlayer player) {
		IThirstCapability thirst = (IThirstCapability) thirstCapability(player);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("level", CTCompatData.integer(thirst.getThirstLevel()));
		data.put("saturation", CTCompatData.floating(thirst.getThirstSaturation()));
		data.put("exhaustion", CTCompatData.floating(thirst.getThirstExhaustion()));
		data.put("tickTimer", CTCompatData.integer(thirst.getThirstTickTimer()));
		data.put("damageCounter", CTCompatData.integer(thirst.getThirstDamageCounter()));
		data.put("isThirsty", CTCompatData.bool(thirst.isThirsty()));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static int getTemperatureLevel(IPlayer player) {
		return ((ITemperatureCapability) temperatureCapability(player)).getTemperatureLevel();
	}

	@ZenMethod
	public static void setTemperatureLevel(IPlayer player, int temperature) {
		((ITemperatureCapability) temperatureCapability(player)).setTemperatureLevel(TemperatureUtil.clampTemperature(temperature));
	}

	@ZenMethod
	public static void addTemperatureLevel(IPlayer player, int amount) {
		ITemperatureCapability capability = (ITemperatureCapability) temperatureCapability(player);
		capability.setTemperatureLevel(TemperatureUtil.clampTemperature(capability.getTemperatureLevel() + amount));
	}

	@ZenMethod
	public static int getTemperatureTickTimer(IPlayer player) {
		return ((ITemperatureCapability) temperatureCapability(player)).getTemperatureTickTimer();
	}

	@ZenMethod
	public static void setTemperatureTickTimer(IPlayer player, int ticks) {
		((ITemperatureCapability) temperatureCapability(player)).setTemperatureTickTimer(ticks);
	}

	@ZenMethod
	public static void addTemperatureTickTimer(IPlayer player, int ticks) {
		((ITemperatureCapability) temperatureCapability(player)).addTemperatureTickTimer(ticks);
	}

	@ZenMethod
	public static int getTemperatureDamageCounter(IPlayer player) {
		return ((ITemperatureCapability) temperatureCapability(player)).getTemperatureDamageCounter();
	}

	@ZenMethod
	public static void setTemperatureDamageCounter(IPlayer player, int value) {
		((ITemperatureCapability) temperatureCapability(player)).setTemperatureDamageCounter(value);
	}

	@ZenMethod
	public static void addTemperatureDamageCounter(IPlayer player, int value) {
		((ITemperatureCapability) temperatureCapability(player)).addTemperatureDamageCounter(value);
	}

	@ZenMethod
	public static String getTemperatureEnum(int temperature) {
		requireLoaded();
		return TemperatureUtil.getTemperatureEnum(TemperatureUtil.clampTemperature(temperature)).name();
	}

	@ZenMethod
	public static IData getTemperatureEnumInfo(String name) {
		requireLoaded();
		TemperatureEnum type = (TemperatureEnum) parseTemperatureEnum(name);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("name", CTCompatData.string(type.name()));
		data.put("lowerBound", CTCompatData.integer(type.getLowerBound()));
		data.put("upperBound", CTCompatData.integer(type.getUpperBound()));
		data.put("middle", CTCompatData.integer(type.getMiddle()));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static String[] getTemperatureEnums() {
		requireLoaded();
		TemperatureEnum[] values = TemperatureEnum.values();
		String[] names = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			names[i] = values[i].name();
		}
		return names;
	}

	@ZenMethod
	public static int getPlayerTargetTemperature(IPlayer player) {
		requireLoaded();
		return TemperatureUtil.getPlayerTargetTemperature(getPlayer(player));
	}

	@ZenMethod
	public static int getWorldTemperature(IPlayer player) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		return TemperatureUtil.getWorldTemperature(mcPlayer.world, mcPlayer.getPosition());
	}

	@ZenMethod
	public static int getWorldTemperature(IPlayer player, int x, int y, int z) {
		requireLoaded();
		EntityPlayer mcPlayer = getPlayer(player);
		return TemperatureUtil.getWorldTemperature(mcPlayer.world, new BlockPos(x, y, z));
	}

	@ZenMethod
	public static IData getTemperatureData(IPlayer player) {
		ITemperatureCapability temperature = (ITemperatureCapability) temperatureCapability(player);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("level", CTCompatData.integer(temperature.getTemperatureLevel()));
		data.put("enum", CTCompatData.string(temperature.getTemperatureEnum().name()));
		data.put("tickTimer", CTCompatData.integer(temperature.getTemperatureTickTimer()));
		data.put("damageCounter", CTCompatData.integer(temperature.getTemperatureDamageCounter()));
		data.put("temporaryModifiers", getTemporaryModifiers(player));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static IData getTemporaryModifiers(IPlayer player) {
		Map<String, IData> data = new LinkedHashMap<>();
		for (Map.Entry<String, TemporaryModifier> entry : ((ITemperatureCapability) temperatureCapability(player)).getTemporaryModifiers().entrySet()) {
			TemporaryModifier modifier = entry.getValue();
			Map<String, IData> modifierData = new LinkedHashMap<>();
			modifierData.put("temperature", CTCompatData.floating(modifier.temperature));
			modifierData.put("duration", CTCompatData.integer(modifier.duration));
			data.put(entry.getKey(), CTCompatData.map(modifierData));
		}
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static void setTemporaryModifier(IPlayer player, String name, float temperature, int duration) {
		((ITemperatureCapability) temperatureCapability(player)).setTemporaryModifier(name, temperature, duration);
	}

	@ZenMethod
	public static void clearTemporaryModifiers(IPlayer player) {
		((ITemperatureCapability) temperatureCapability(player)).clearTemporaryModifiers();
	}

	@ZenMethod
	public static void setArmorTemperature(IItemStack stack, float temperature) {
		requireLoaded();
		TemperatureUtil.setArmorTemperatureTag(getStack(stack), temperature);
	}

	@ZenMethod
	public static float getArmorTemperature(IItemStack stack) {
		requireLoaded();
		return TemperatureUtil.getArmorTemperatureTag(getStack(stack));
	}

	@ZenMethod
	public static void removeArmorTemperature(IItemStack stack) {
		requireLoaded();
		TemperatureUtil.removeArmorTemperatureTag(getStack(stack));
	}

	@ZenMethod
	public static String[] getThirstTypes() {
		requireLoaded();
		ThirstEnum[] values = ThirstEnum.values();
		String[] names = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			names[i] = values[i].name();
		}
		return names;
	}

	@ZenMethod
	public static IData getThirstTypeInfo(String name) {
		requireLoaded();
		ThirstEnum type = (ThirstEnum) parseThirstEnum(name);
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("name", CTCompatData.string(type.name()));
		data.put("id", CTCompatData.string(type.getName()));
		data.put("thirst", CTCompatData.integer(type.getThirst()));
		data.put("saturation", CTCompatData.floating(type.getSaturation()));
		data.put("thirstyChance", CTCompatData.floating(type.getThirstyChance()));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static void takeDrink(IPlayer player, int thirst, float saturation, float thirstyChance) {
		requireLoaded();
		ThirstUtil.takeDrink(getPlayer(player), thirst, saturation, thirstyChance);
	}

	@ZenMethod
	public static void takeDrink(IPlayer player, int thirst, float saturation) {
		requireLoaded();
		ThirstUtil.takeDrink(getPlayer(player), thirst, saturation);
	}

	@ZenMethod
	public static void takeDrink(IPlayer player, String thirstType) {
		requireLoaded();
		ThirstUtil.takeDrink(getPlayer(player), (ThirstEnum) parseThirstEnum(thirstType));
	}

	@ZenMethod
	public static IData getConsumableThirst(IItemStack food) {
		requireLoaded();
		Object entry = findConsumableThirst(getStack(food));
		if (entry == null) {
			return emptyConsumableData();
		}
		JsonConsumableThirst thirst = (JsonConsumableThirst) entry;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("matched", CTCompatData.bool(true));
		data.put("thirst", CTCompatData.integer(thirst.amount));
		data.put("saturation", CTCompatData.floating(thirst.saturation));
		data.put("thirstyChance", CTCompatData.floating(thirst.thirstyChance));
		data.put("identity", identityData(thirst.identity));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static IData getConsumableTemperature(IItemStack food) {
		requireLoaded();
		Object entry = findConsumableTemperature(getStack(food));
		if (entry == null) {
			Map<String, IData> data = new LinkedHashMap<>();
			data.put("matched", CTCompatData.bool(false));
			return CTCompatData.map(data);
		}
		JsonConsumableTemperature temperature = (JsonConsumableTemperature) entry;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("matched", CTCompatData.bool(true));
		data.put("group", CTCompatData.string(temperature.group));
		data.put("temperature", CTCompatData.floating(temperature.temperature));
		data.put("duration", CTCompatData.integer(temperature.duration));
		data.put("identity", identityData(temperature.identity));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static IData getFoodData(IItemStack food) {
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("thirst", getConsumableThirst(food));
		data.put("temperature", getConsumableTemperature(food));
		return CTCompatData.map(data);
	}

	@ZenMethod
	public static void registerArmorTemperature(IItemStack stack, float temperature) {
		requireLoaded();
		JsonConfig.registerArmorTemperature(getStack(stack), temperature);
	}

	@ZenMethod
	public static void registerArmorTemperatureByName(String registryName, float temperature) {
		registerArmorTemperatureByName(registryName, temperature, -1, null);
	}

	@ZenMethod
	public static void registerArmorTemperatureByName(String registryName, float temperature, int metadata) {
		registerArmorTemperatureByName(registryName, temperature, metadata, null);
	}

	@ZenMethod
	public static void registerArmorTemperatureByName(String registryName, float temperature, int metadata, String nbt) {
		requireLoaded();
		JsonConfig.registerArmorTemperature(registryName, temperature,
				(JsonItemIdentity) createIdentity(metadata, nbt));
	}

	@ZenMethod
	public static boolean registerBlockTemperature(String registryName, float temperature) {
		requireLoaded();
		return JsonConfig.registerBlockTemperature(registryName, temperature, new JsonPropertyValue[0]);
	}

	@ZenMethod
	public static boolean registerBlockTemperatureWithProperties(String registryName, float temperature,
			IData properties) {
		requireLoaded();
		return JsonConfig.registerBlockTemperature(registryName, temperature,
				(JsonPropertyValue[]) createPropertyValues(properties));
	}

	@ZenMethod
	public static void registerFluidTemperature(String fluidName, float temperature) {
		requireLoaded();
		JsonConfig.registerFluidTemperature(fluidName, temperature);
	}

	@ZenMethod
	public static void registerConsumableTemperature(String group, IItemStack food, float temperature, int duration) {
		requireLoaded();
		JsonConfig.registerConsumableTemperature(group, getStack(food), temperature, duration);
	}

	@ZenMethod
	public static void registerConsumableTemperatureByName(String group, String registryName, float temperature,
			int duration) {
		registerConsumableTemperatureByName(group, registryName, temperature, duration, -1, null);
	}

	@ZenMethod
	public static void registerConsumableTemperatureByName(String group, String registryName, float temperature,
			int duration, int metadata) {
		registerConsumableTemperatureByName(group, registryName, temperature, duration, metadata, null);
	}

	@ZenMethod
	public static void registerConsumableTemperatureByName(String group, String registryName, float temperature,
			int duration, int metadata, String nbt) {
		requireLoaded();
		JsonConfig.registerConsumableTemperature(group, registryName, temperature, duration,
				(JsonItemIdentity) createIdentity(metadata, nbt));
	}

	@ZenMethod
	public static void registerConsumableThirst(IItemStack food, int amount, float saturation,
			float thirstyChance) {
		requireLoaded();
		JsonConfig.registerConsumableThirst(getStack(food), amount, saturation, thirstyChance);
	}

	@ZenMethod
	public static void registerConsumableThirstByName(String registryName, int amount, float saturation,
			float thirstyChance) {
		registerConsumableThirstByName(registryName, amount, saturation, thirstyChance, -1, null);
	}

	@ZenMethod
	public static void registerConsumableThirstByName(String registryName, int amount, float saturation,
			float thirstyChance, int metadata) {
		registerConsumableThirstByName(registryName, amount, saturation, thirstyChance, metadata, null);
	}

	@ZenMethod
	public static void registerConsumableThirstByName(String registryName, int amount, float saturation,
			float thirstyChance, int metadata, String nbt) {
		requireLoaded();
		JsonConfig.registerConsumableThirst(registryName, amount, saturation, thirstyChance,
				(JsonItemIdentity) createIdentity(metadata, nbt));
	}

	@ZenMethod
	public static void registerHeldItem(IItemStack stack, float temperature) {
		requireLoaded();
		JsonConfig.registerHeldItem(getStack(stack), temperature);
	}

	@ZenMethod
	public static void registerHeldItemByName(String registryName, float temperature) {
		registerHeldItemByName(registryName, temperature, -1, null);
	}

	@ZenMethod
	public static void registerHeldItemByName(String registryName, float temperature, int metadata) {
		registerHeldItemByName(registryName, temperature, metadata, null);
	}

	@ZenMethod
	public static void registerHeldItemByName(String registryName, float temperature, int metadata, String nbt) {
		requireLoaded();
		JsonConfig.registerHeldItem(registryName, temperature, (JsonItemIdentity) createIdentity(metadata, nbt));
	}

	@ZenMethod
	public static void registerDimensionTemperature(int dimension, float temperature) {
		requireLoaded();
		JsonConfig.registerDimensionTemperature(dimension, temperature);
	}

	@ZenMethod
	public static void registerDimensionTemperatureByName(String dimension, float temperature) {
		requireLoaded();
		JsonConfig.registerDimensionTemperature(dimension, temperature);
	}

	public static boolean isSupportedDrink(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		if (stack.getItem() instanceof ItemDrinkBase || findConsumableThirst(stack) != null) {
			return true;
		}
		if (stack.getItem() != Items.POTIONITEM) {
			return false;
		}
		PotionType potion = PotionUtils.getPotionFromItem(stack);
		return potion != null && potion.getRegistryName() != null && !"empty".equals(potion.getRegistryName().getPath());
	}

	private static Object findConsumableThirst(ItemStack stack) {
		if (stack == null || stack.isEmpty() || stack.getItem().getRegistryName() == null) {
			return null;
		}
		java.util.List<?> entries = JsonConfig.consumableThirst.get(stack.getItem().getRegistryName().toString());
		if (entries != null) {
			for (Object entry : entries) {
				if (entry instanceof JsonConsumableThirst && ((JsonConsumableThirst) entry).matches(stack)) {
					return entry;
				}
			}
		}
		return null;
	}

	private static Object findConsumableTemperature(ItemStack stack) {
		if (stack == null || stack.isEmpty() || stack.getItem().getRegistryName() == null) {
			return null;
		}
		java.util.List<?> entries = JsonConfig.consumableTemperature.get(stack.getItem().getRegistryName().toString());
		if (entries != null) {
			for (Object entry : entries) {
				if (entry instanceof JsonConsumableTemperature && ((JsonConsumableTemperature) entry).matches(stack)) {
					return entry;
				}
			}
		}
		return null;
	}

	private static IData emptyConsumableData() {
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("matched", CTCompatData.bool(false));
		return CTCompatData.map(data);
	}

	private static IData identityData(Object identityObject) {
		if (identityObject == null) {
			return CTCompatData.emptyMap();
		}
		JsonItemIdentity identity = (JsonItemIdentity) identityObject;
		Map<String, IData> data = new LinkedHashMap<>();
		data.put("metadata", CTCompatData.integer(identity.metadata));
		data.put("nbt", CTCompatData.string(identity.nbt));
		return CTCompatData.map(data);
	}

	private static Object createIdentity(int metadata, String nbt) {
		return new JsonItemIdentity(metadata, nbt);
	}

	private static Object createPropertyValues(IData properties) {
		if (properties == null) {
			return new JsonPropertyValue[0];
		}
		java.util.List<JsonPropertyValue> values = new java.util.ArrayList<>();
		for (Map.Entry<String, IData> entry : properties.asMap().entrySet()) {
			values.add(new JsonPropertyValue(entry.getKey(), entry.getValue().asString()));
		}
		return values.toArray(new JsonPropertyValue[0]);
	}

	private static Object parseTemperatureEnum(String name) {
		try {
			return TemperatureEnum.valueOf(normalizeEnumName(name));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown SimpleDifficulty temperature type '" + name + "'.", e);
		}
	}

	private static Object parseThirstEnum(String name) {
		try {
			return ThirstEnum.valueOf(normalizeEnumName(name));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unknown SimpleDifficulty thirst type '" + name + "'.", e);
		}
	}

	private static String normalizeEnumName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Enum name cannot be null.");
		}
		return name.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
	}

	private static ItemStack getStack(IItemStack stack) {
		return stack == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(stack);
	}

	private static EntityPlayer getPlayer(IPlayer player) {
		return CraftTweakerMC.getPlayer(player);
	}

	private static Object thirstCapability(IPlayer player) {
		requireLoaded();
		IThirstCapability capability = SDCapabilities.getThirstData(getPlayer(player));
		if (capability == null) {
			throw new IllegalStateException("SimpleDifficulty thirst capability is unavailable for this player.");
		}
		return capability;
	}

	private static Object temperatureCapability(IPlayer player) {
		requireLoaded();
		ITemperatureCapability capability = SDCapabilities.getTemperatureData(getPlayer(player));
		if (capability == null) {
			throw new IllegalStateException("SimpleDifficulty temperature capability is unavailable for this player.");
		}
		return capability;
	}

	private static void requireLoaded() {
		if (!isLoaded()) {
			throw new IllegalStateException("SimpleDifficulty is not loaded.");
		}
	}
}
