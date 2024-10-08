package org.carpet_org_addition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.wheel.SingleThingCounter;

import java.util.ArrayList;

public class FishingData extends AbstractActionData {
    private final SingleThingCounter timer = new SingleThingCounter();

    public FishingData() {
    }

    @Override
    public ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        list.add(TextUtils.translate("carpet.commands.playerAction.info.fishing", fakePlayer.getDisplayName()));
        return list;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject();
    }

    public SingleThingCounter getTimer() {
        return timer;
    }
}
