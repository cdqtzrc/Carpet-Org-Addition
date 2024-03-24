package org.carpet_org_addition.test;

import carpet.api.settings.Rule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.translate.Translate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TranslateTest {

    // 列出所有已启用但还存在于翻译文件中的规则
    @Test
    public void notUsedTranslateKey() throws IOException {
        HashSet<String> hashSet = new HashSet<>();
        Class<CarpetOrgAdditionSettings> clazz = CarpetOrgAdditionSettings.class;
        Field[] fields = clazz.getDeclaredFields();
        List<String> list = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(Rule.class)).map(Field::getName).toList();
        String json = IOUtils.toString(Objects.requireNonNull(Translate.class.getClassLoader().getResourceAsStream(
                "assets/carpet-org-addition/lang/en_us.json")), StandardCharsets.UTF_8);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        Map<String, String> translate = gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
        for (Map.Entry<String, String> entry : translate.entrySet()) {
            String[] notUsed = entry.getKey().split("\\.");
            if (notUsed[1].equals("rule")) {
                if (list.contains(notUsed[2]) || hashSet.contains(notUsed[2])) {
                    continue;
                }
                hashSet.add(notUsed[2]);
                System.out.println(notUsed[2]);
            }
        }
    }
}
