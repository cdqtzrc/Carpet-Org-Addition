package org.carpet_org_addition.translate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class TranslateTest {
    /**
     * 比较中英文翻译的键是否完全相同
     */
    @Test
    public void compareTranslationKey() throws IOException {
        BufferedReader enReader = new BufferedReader(new FileReader("src/main/resources/assets/carpet-org-addition/lang/en_us.json"));
        BufferedReader zhReader = new BufferedReader(new FileReader("src/main/resources/assets/carpet-org-addition/lang/zh_cn.json"));
        Gson gson = new Gson();
        JsonObject jsonEn = gson.fromJson(enReader, JsonObject.class);
        JsonObject jsonZh = gson.fromJson(zhReader, JsonObject.class);
        Predicate<String> predicate = s -> !s.matches("carpet\\.rule\\..+\\.name");
        List<String> zh = jsonZh.entrySet().stream().map(Map.Entry::getKey).filter(predicate).toList();
        List<String> en = jsonEn.entrySet().stream().map(Map.Entry::getKey).filter(predicate).toList();
        // 中英文翻译的差异
        ArrayList<String> difference = new ArrayList<>();
        // 包含在中文翻译中，但在英文翻译中没有
        for (String key : zh) {
            if (en.contains(key)) {
                continue;
            }
            difference.add("en_us缺失：" + key);
        }
        // 包含在英文翻译中，但在中文翻译中没有
        for (String key : en) {
            if (zh.contains(key)) {
                continue;
            }
            difference.add("zh_cn缺失：" + key);
        }
        StringJoiner errorReport = new StringJoiner("\n", "中英文翻译键不匹配：\n", "");
        difference.forEach(errorReport::add);
        Assert.assertTrue(errorReport.toString(), difference.isEmpty());
    }
}
