package org.carpet_org_addition.util.fakeplayer.actiondata;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.util.helpers.JsonSerial;

public class SortingData implements JsonSerial {
    private final Item item;
    private final Vec3d thisVec;
    private final Vec3d otherVec;

    public SortingData(Item item, Vec3d thisVec, Vec3d otherVec) {
        this.item = item;
        this.thisVec = thisVec;
        this.otherVec = otherVec;
    }

    public Item getItem() {
        return item;
    }

    {
        MinecraftServer server;

    }

    public Vec3d getThisVec() {
        return thisVec;
    }

    public Vec3d getOtherVec() {
        return otherVec;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        // 要分拣的物品
        json.addProperty("item", Registries.ITEM.getId(this.item).toString());
        // 当前物品要丢弃的位置
        JsonArray thisVecJson = new JsonArray();
        thisVecJson.add(this.thisVec.x);
        thisVecJson.add(this.thisVec.y);
        thisVecJson.add(this.thisVec.z);
        json.add("thisVec", thisVecJson);
        // 其它物品要丢弃的位置
        JsonArray otherVecJson = new JsonArray();
        otherVecJson.add(this.otherVec.x);
        otherVecJson.add(this.otherVec.y);
        otherVecJson.add(this.otherVec.z);
        json.add("otherVec", otherVecJson);
        return json;
    }
}
