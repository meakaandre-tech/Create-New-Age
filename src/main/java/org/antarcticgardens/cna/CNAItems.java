package org.antarcticgardens.cna;

import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.antarcticgardens.cna.registry.ItemEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Items. Their tags come from the generated data. */
public class CNAItems {
    /** Items shown in the creative tab, in registration order (incomplete assembly items are left out). */
    public static final List<ItemEntry<?>> TAB = new ArrayList<>();

    public static final ItemEntry<Item> OVERCHARGED_GOLD = item("overcharged_gold", Item::new);
    public static final ItemEntry<Item> OVERCHARGED_IRON = item("overcharged_iron", Item::new);
    public static final ItemEntry<Item> OVERCHARGED_DIAMOND = item("overcharged_diamond", Item::new);
    public static final ItemEntry<Item> NUCLEAR_FUEL = item("nuclear_fuel", Item::new);
    public static final ItemEntry<Item> THORIUM = item("thorium", Item::new);
    public static final ItemEntry<Item> RADIOACTIVE_THORIUM = item("radioactive_thorium", Item::new);

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_FUEL = hidden("incomplete_fuel", SequencedAssemblyItem::new);
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_REACTOR_CASING = hidden("incomplete_reactor_casing", SequencedAssemblyItem::new);
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_WIRE = hidden("incomplete_wire", SequencedAssemblyItem::new);
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_ENCHANTED_GOLDEN_APPLE = hidden("incomplete_enchanted_golden_apple", SequencedAssemblyItem::new);

    public static final ItemEntry<Item> OVERCHARGED_IRON_SHEET = item("overcharged_iron_sheet", Item::new);
    public static final ItemEntry<Item> OVERCHARGED_GOLDEN_SHEET = item("overcharged_golden_sheet", Item::new);
    public static final ItemEntry<Item> BLANK_CIRCUIT = item("blank_circuit", Item::new);
    public static final ItemEntry<Item> COPPER_CIRCUIT = item("copper_circuit", Item::new);

    public static final ItemEntry<ElectricWireItem> COPPER_WIRE = item("copper_wire", ElectricWireItem::newCopperWire);
    public static final ItemEntry<ElectricWireItem> OVERCHARGED_IRON_WIRE = item("overcharged_iron_wire", ElectricWireItem::newIronWire);
    public static final ItemEntry<ElectricWireItem> OVERCHARGED_GOLDEN_WIRE = item("overcharged_golden_wire", ElectricWireItem::newGoldenWire);
    public static final ItemEntry<ElectricWireItem> OVERCHARGED_DIAMOND_WIRE = item("overcharged_diamond_wire", ElectricWireItem::newDiamondWire);

    private static <T extends Item> ItemEntry<T> item(String name, Function<Item.Properties, T> factory) {
        ItemEntry<T> entry = hidden(name, factory);
        TAB.add(entry);
        return entry;
    }

    private static <T extends Item> ItemEntry<T> hidden(String name, Function<Item.Properties, T> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, CreateNewAge.asResource(name));
        return new ItemEntry<>(Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key))));
    }

    public static void register() {
    }
}
