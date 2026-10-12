package org.antarcticgardens.cna.client;

import com.zurrtum.create.client.foundation.block.connected.AllCTTypes;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter;
import com.zurrtum.create.client.foundation.block.connected.CTType;
import org.antarcticgardens.cna.CreateNewAge;

public class CNASpriteShifts {
    public static final CTSpriteShiftEntry HEAT_CASING = omni("heat_casing");
    public static final CTSpriteShiftEntry REACTOR_CASING = omni("reactor_casing");
    public static final CTSpriteShiftEntry REACTOR_GLASS = omni("reactor_glass");
    public static final CTSpriteShiftEntry REDSTONE_MAGNET = omni("redstone_magnet");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String name) {
        return CTSpriteShifter.getCT(type, CreateNewAge.asResource("block/" + name), CreateNewAge.asResource("block/" + name + "_connected"));
    }

    public static void init() {
    }
}
