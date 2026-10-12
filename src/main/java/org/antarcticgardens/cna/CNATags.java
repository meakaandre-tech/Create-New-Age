package org.antarcticgardens.cna;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.Locale;

/** Tag keys. The tag contents are in the generated data. */
public class CNATags {
    public enum Block {
        MAGNET(true),
        STOPS_RADIATION(false);

        public final boolean hasItemTag;
        public final TagKey<net.minecraft.world.level.block.Block> blockTag;
        public final TagKey<net.minecraft.world.item.Item> itemTag;

        Block(boolean hasItemTag) {
            Identifier location = CreateNewAge.asResource(name().toLowerCase(Locale.ROOT));
            this.hasItemTag = hasItemTag;
            this.blockTag = TagKey.create(Registries.BLOCK, location);
            this.itemTag = hasItemTag ? TagKey.create(Registries.ITEM, location) : null;
        }
    }

    public enum Item {
        NUCLEAR_FUEL("nuclear/fuel"),
        HAZMAT_SUIT("hazmat_suit");

        public final TagKey<net.minecraft.world.item.Item> tag;

        Item(String id) {
            tag = TagKey.create(Registries.ITEM, CreateNewAge.asResource(id));
        }
    }

    public static TagKey<net.minecraft.world.item.Item> createNuclearEnergyTag(int energy) {
        return TagKey.create(Registries.ITEM, CreateNewAge.asResource("nuclear/energy_" + energy));
    }

    public static TagKey<net.minecraft.world.level.block.Block> createMagneticForgeTag(int force) {
        return TagKey.create(Registries.BLOCK, CreateNewAge.asResource("magnet/force_" + force));
    }
}
