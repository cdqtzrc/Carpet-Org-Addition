package org.carpet_org_addition.util.wheel;

import com.google.gson.JsonObject;
import org.carpet_org_addition.util.IOUtils;
import org.junit.Test;

public class WorldFormatTest {
    @Test
    public void getJsonElementTest() {
        JsonObject json = new JsonObject();
        json.addProperty("aaa", "bbb");
        json.addProperty("Number1", 100);
        json.addProperty("Number2", 100.0);
        json.addProperty("Number3", 100F);
        json.addProperty("Boolean", true);
        System.out.println(IOUtils.getJsonElement(json, "aaa", "BBB", String.class));
        System.out.println(IOUtils.getJsonElement(json, "Number1", 0, Number.class));
        System.out.println(IOUtils.getJsonElement(json, "Number2", 0, Number.class));
        System.out.println(IOUtils.getJsonElement(json, "Number3", 0, Number.class));
        System.out.println(IOUtils.getJsonElement(json, "Boolean", false, Boolean.class));
        System.out.println(IOUtils.getJsonElement(json, "_aaa", "BBB", String.class));
        System.out.println(IOUtils.getJsonElement(json, "_Number1", 0, Number.class));
        System.out.println(IOUtils.getJsonElement(json, "_Number2", 0, Number.class));
        System.out.println(IOUtils.getJsonElement(json, "_Number3", 0, Number.class));
        System.out.println(IOUtils.getJsonElement(json, "_Boolean", false, Boolean.class));
    }
}
