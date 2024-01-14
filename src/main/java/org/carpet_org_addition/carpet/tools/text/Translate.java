package org.carpet_org_addition.carpet.tools.text;

import carpet.CarpetSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Translate {
    // TODO 用集合改进
    public static final HashMap<String, Map<String, String>> TRANSLATE = new HashMap<>();

    //获取翻译
    public static Map<String, String> getTranslate() {
        if (TRANSLATE.containsKey(CarpetSettings.language)) {
            return TRANSLATE.get(CarpetSettings.language);
        }
        String dataJSON;
        ClassLoader classLoader = Translate.class.getClassLoader();
        try {
            dataJSON = IOUtils.toString(Objects.requireNonNull(
                    classLoader.getResourceAsStream(
                            "assets/carpet-org-addition/lang/" + CarpetSettings.language + ".json")
            ), StandardCharsets.UTF_8);
        } catch (NullPointerException | IOException e) {
            try {
                dataJSON = IOUtils.toString(
                        Objects.requireNonNull(classLoader.getResourceAsStream(
                                "assets/carpet-org-addition/lang/en_us.json")),
                        StandardCharsets.UTF_8);
            } catch (NullPointerException | IOException ex) {
                return null;
            }
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, String> translate = gson.fromJson(dataJSON, new TypeToken<Map<String, String>>() {
        }.getType());
        TRANSLATE.put(CarpetSettings.language, translate);
        return translate;
    }
}
