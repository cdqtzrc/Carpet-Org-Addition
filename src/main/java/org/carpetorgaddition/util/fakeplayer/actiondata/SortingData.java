package org.carpetorgaddition.util.fakeplayer.actiondata;

import carpet.patches.EntityPlayerMPFake;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.carpetorgaddition.util.MathUtils;
import org.carpetorgaddition.util.TextUtils;
import org.carpetorgaddition.util.matcher.Matcher;

import java.util.ArrayList;

public class SortingData extends AbstractActionData {

    private static final String ITEM = "item";
    private static final String THIS_VEC = "thisVec";
    private static final String OTHER_VEC = "otherVec";
    /**
     * 要分拣的物品
     */
    private final Item item;
    /**
     * 如果当前物品是要分拣的物品，则将该物品向这个方向丢出
     */
    private final Vec3d thisVec;
    /**
     * 如果当前物品不是要分拣的物品，则将该物品向这个方向丢出
     */
    private final Vec3d otherVec;

    public SortingData(Item item, Vec3d thisVec, Vec3d otherVec) {
        this.item = item;
        this.thisVec = thisVec;
        this.otherVec = otherVec;
    }

    public static SortingData load(JsonObject json) {
        String id = json.get(ITEM).getAsString();
        Item item = Matcher.asItem(id);
        JsonArray thisVecArray = json.get(THIS_VEC).getAsJsonArray();
        Vec3d thisVec = new Vec3d(
                thisVecArray.get(0).getAsDouble(),
                thisVecArray.get(1).getAsDouble(),
                thisVecArray.get(2).getAsDouble());
        JsonArray otherVecArray = json.get(OTHER_VEC).getAsJsonArray();
        Vec3d otherVec = new Vec3d(
                otherVecArray.get(0).getAsDouble(),
                otherVecArray.get(1).getAsDouble(),
                otherVecArray.get(2).getAsDouble());
        return new SortingData(item, thisVec, otherVec);
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        // 要分拣的物品
        json.addProperty(ITEM, Registries.ITEM.getId(this.item).toString());
        // 当前物品要丢弃的位置
        JsonArray thisVecJson = new JsonArray();
        thisVecJson.add(this.thisVec.x);
        thisVecJson.add(this.thisVec.y);
        thisVecJson.add(this.thisVec.z);
        json.add(THIS_VEC, thisVecJson);
        // 其它物品要丢弃的位置
        JsonArray otherVecJson = new JsonArray();
        otherVecJson.add(this.otherVec.x);
        otherVecJson.add(this.otherVec.y);
        otherVecJson.add(this.otherVec.z);
        json.add(OTHER_VEC, otherVecJson);
        return json;
    }

    @Override
    public ArrayList<MutableText> info(EntityPlayerMPFake fakePlayer) {
        ArrayList<MutableText> list = new ArrayList<>();
        // 获取要分拣的物品名称
        Text itemName = this.item.getDefaultStack().toHoverableText();
        // 获取假玩家的显示名称
        Text fakeName = fakePlayer.getDisplayName();
        // 将假玩家正在分拣物品的消息添加到集合中
        list.add(TextUtils.translate("carpet.commands.playerAction.info.sorting.item", fakeName, itemName));
        // 获取分拣物品要丢出的方向
        MutableText thisPos = Text.literal(MathUtils.keepTwoDecimalPlaces(thisVec.getX(), thisVec.getY(), thisVec.getZ()));
        // 获取非分拣物品要丢出的方向
        MutableText otherPos = Text.literal(MathUtils.keepTwoDecimalPlaces(otherVec.getX(), otherVec.getY(), otherVec.getZ()));
        // 将丢要分拣物品的方向的信息添加到集合
        list.add(TextUtils.translate("carpet.commands.playerAction.info.sorting.this", itemName, thisPos));
        // 将丢其他物品的方向的信息添加到集合
        list.add(TextUtils.translate("carpet.commands.playerAction.info.sorting.other", otherPos));
        return list;
    }

    public Item getItem() {
        return item;
    }

    public Vec3d getThisVec() {
        return thisVec;
    }

    public Vec3d getOtherVec() {
        return otherVec;
    }
}
