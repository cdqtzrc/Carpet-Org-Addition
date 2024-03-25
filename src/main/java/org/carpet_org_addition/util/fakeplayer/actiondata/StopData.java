package org.carpet_org_addition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import net.minecraft.text.MutableText;
import org.carpet_org_addition.util.TextUtils;

import java.util.ArrayList;

public class StopData extends AbstractActionData {
    public static final StopData STOP = new StopData();

    private StopData() {
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject();
    }

    @Override
    public ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 直接将假玩家没有任何动作的信息加入集合然后返回
        list.add(TextUtils.getTranslate("carpet.commands.playerAction.info.stop", fakePlayer.getDisplayName()));
        return list;
    }
}
