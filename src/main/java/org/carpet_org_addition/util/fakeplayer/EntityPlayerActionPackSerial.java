package org.carpet_org_addition.util.fakeplayer;

import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.helpers.EntityPlayerActionPack.Action;
import carpet.helpers.EntityPlayerActionPack.ActionType;
import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonObject;
import org.carpet_org_addition.mixin.rule.entityplayeractionpack.ActionAccessor;
import org.carpet_org_addition.mixin.rule.entityplayeractionpack.EntityPlayerActionPackAccessor;
import org.carpet_org_addition.util.wheel.JsonSerial;

import java.util.Map;

public class EntityPlayerActionPackSerial implements JsonSerial {
    private final Map<ActionType, EntityPlayerActionPack.Action> actionMap;

    public EntityPlayerActionPackSerial(EntityPlayerActionPack actionPack) {
        this.actionMap = ((EntityPlayerActionPackAccessor) actionPack).getActions();
    }

    public static void startAction(EntityPlayerMPFake fakePlayer, JsonObject json) {
        EntityPlayerActionPack action = ((ServerPlayerInterface) fakePlayer).getActionPack();
        // 设置假玩家左键
        if (json.has("attack")) {
            JsonObject attack = json.get("attack").getAsJsonObject();
            if (attack.get("continuous").getAsBoolean()) {
                // 左键长按
                action.start(ActionType.ATTACK, Action.continuous());
            } else {
                // 间隔左键
                int interval = attack.get("interval").getAsInt();
                action.start(ActionType.ATTACK, Action.interval(interval));
            }
        }
        // 设置假玩家右键
        if (json.has("use")) {
            JsonObject attack = json.get("use").getAsJsonObject();
            if (attack.get("continuous").getAsBoolean()) {
                // 右键长按
                action.start(ActionType.USE, Action.continuous());
            } else {
                // 间隔右键
                int interval = attack.get("interval").getAsInt();
                action.start(ActionType.USE, Action.interval(interval));
            }
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        // 左键动作
        Action attack = this.actionMap.get(ActionType.ATTACK);
        if (attack != null && !attack.done) {
            JsonObject attackJson = new JsonObject();
            attackJson.addProperty("interval", attack.interval);
            attackJson.addProperty("continuous", ((ActionAccessor) attack).isContinuous());
            json.add("attack", attackJson);
        }
        // 右键动作
        Action use = this.actionMap.get(ActionType.USE);
        if (use != null && !use.done) {
            JsonObject useJson = new JsonObject();
            useJson.addProperty("interval", use.interval);
            useJson.addProperty("continuous", ((ActionAccessor) use).isContinuous());
            json.add("use", useJson);
        }
        return json;
    }
}
