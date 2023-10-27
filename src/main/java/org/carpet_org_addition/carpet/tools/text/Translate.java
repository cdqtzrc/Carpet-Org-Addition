package org.carpet_org_addition.carpet.tools.text;

import carpet.CarpetSettings;
import carpet.utils.Translations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class Translate {
    //获取翻译
    public static Map<String, String> getTranslate() {
        String dataJSON;
        try {
            dataJSON = IOUtils.toString(
                    Objects.requireNonNull(
                            Translations.class
                                    .getClassLoader()
                                    .getResourceAsStream(String.format("assets/carpet-org-addition/lang/%s.json", CarpetSettings.language))
                    ),
                    StandardCharsets.UTF_8
            );
        } catch (NullPointerException | IOException e) {
            try {
                dataJSON = IOUtils.toString(
                        Objects.requireNonNull(
                                Translations.class
                                        .getClassLoader()
                                        .getResourceAsStream("assets/carpet-org-addition/lang/en_us.json")
                        ),
                        StandardCharsets.UTF_8
                );
            } catch (NullPointerException | IOException ex) {
                return null;
            }
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.fromJson(dataJSON, new TypeToken<Map<String, String>>() {
        }.getType());
    }
}
