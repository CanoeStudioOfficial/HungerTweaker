package coolsquid.hungertweaker.ct.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import crafttweaker.api.data.DataBool;
import crafttweaker.api.data.DataDouble;
import crafttweaker.api.data.DataFloat;
import crafttweaker.api.data.DataInt;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.DataString;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import squeek.applecore.api.food.FoodValues;

final class CTCompatData {

	private CTCompatData() {
	}

	static IData bool(boolean value) {
		return new DataBool(value);
	}

	static IData integer(int value) {
		return new DataInt(value);
	}

	static IData floating(float value) {
		return new DataFloat(value);
	}

	static IData doubleValue(double value) {
		return new DataDouble(value);
	}

	static IData longValue(long value) {
		return new DataLong(value);
	}

	static IData string(String value) {
		return new DataString(value == null ? "" : value);
	}

	static IData map(Map<String, IData> value) {
		return new DataMap(value, true);
	}

	static IData emptyMap() {
		return DataMap.EMPTY;
	}

	static IData list(Collection<IData> values) {
		return new DataList(new ArrayList<>(values), true);
	}

	static IData strings(Collection<String> values) {
		List<IData> data = new ArrayList<>();
		for (String value : values) {
			data.add(string(value));
		}
		return list(data);
	}

	static IData ints(int[] values) {
		List<IData> data = new ArrayList<>();
		for (int value : values) {
			data.add(integer(value));
		}
		return list(data);
	}

	static IData foodValues(FoodValues values) {
		Map<String, IData> data = new LinkedHashMap<>();
		if (values == null) {
			data.put("hunger", integer(0));
			data.put("saturationModifier", floating(0));
			data.put("saturationIncrement", floating(0));
			return map(data);
		}
		data.put("hunger", integer(values.hunger));
		data.put("saturationModifier", floating(values.saturationModifier));
		data.put("saturationIncrement", floating(values.getUnboundedSaturationIncrement()));
		return map(data);
	}

	static IData itemStack(ItemStack stack) {
		Map<String, IData> data = new LinkedHashMap<>();
		if (stack == null || stack.isEmpty()) {
			data.put("empty", bool(true));
			data.put("commandString", string(""));
			return map(data);
		}
		IItemStack ctStack = CraftTweakerMC.getIItemStack(stack);
		data.put("empty", bool(false));
		data.put("commandString", string(ctStack.toCommandString()));
		data.put("displayName", string(stack.getDisplayName()));
		data.put("amount", integer(stack.getCount()));
		data.put("metadata", integer(stack.getMetadata()));
		return map(data);
	}

	static IData itemStacks(Collection<ItemStack> stacks) {
		List<IData> data = new ArrayList<>();
		for (ItemStack stack : stacks) {
			data.add(itemStack(stack));
		}
		return list(data);
	}
}
