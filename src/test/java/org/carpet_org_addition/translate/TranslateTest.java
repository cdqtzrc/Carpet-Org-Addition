package org.carpet_org_addition.translate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.carpet_org_addition.util.wheel.Counter;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class TranslateTest {
    private final List<String> zh;
    private final List<String> en;
    private final ArrayList<String> notRuleKey = new ArrayList<>();

    public TranslateTest() throws FileNotFoundException {
        BufferedReader enReader = new BufferedReader(new FileReader("src/main/resources/assets/carpet-org-addition/lang/en_us.json"));
        BufferedReader zhReader = new BufferedReader(new FileReader("src/main/resources/assets/carpet-org-addition/lang/zh_cn.json"));
        Gson gson = new Gson();
        JsonObject jsonEn = gson.fromJson(enReader, JsonObject.class);
        JsonObject jsonZh = gson.fromJson(zhReader, JsonObject.class);
        Predicate<String> predicate = s -> !s.matches("carpet\\.rule\\..+\\.name");
        this.zh = jsonZh.entrySet().stream().map(Map.Entry::getKey).filter(predicate).toList();
        this.en = jsonEn.entrySet().stream().map(Map.Entry::getKey).filter(predicate).toList();
        for (String key : zh) {
            if (key.startsWith("carpet.rule.")) {
                continue;
            }
            notRuleKey.add(key);
        }
        // 删除carpet.category.ORG，因为它在源码中没有被直接使用
        notRuleKey.remove("carpet.category.ORG");
    }

    /**
     * 比较中英文翻译的键是否完全相同
     */
    @Test
    public void compareTranslationKey() {
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

    /**
     * 检查是否有翻译键未被使用
     */
    @Test
    public void translationUsedBy() throws IOException {
        File rootPath = new File("src/main/java/org/carpet_org_addition");
        Counter<String> counter = new Counter<>();
        notRuleKey.forEach(key -> counter.set(key, 0));
        translationUsedBy(rootPath, counter);
        // 所有未被使用的翻译键
        List<String> list = counter.stream().filter(s -> counter.getCount(s) == 0).toList();
        StringJoiner errorReport = new StringJoiner("\n", "包含未使用的翻译键", "");
        for (String key : list) {
            errorReport.add(key);
        }
        Assert.assertTrue(errorReport.toString(), list.isEmpty());
    }

    // 递归遍历所有源代码文件
    private void translationUsedBy(File root, Counter<String> counter) throws IOException {
        // 检查当前文件是否是文件夹
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                // 检查当前文件是否是Java文件
                if (file.getName().endsWith(".java")) {
                    readFile(file, counter);
                }
            } else if (file.isDirectory()) {
                // 递归遍历所有Java文件
                translationUsedBy(file, counter);
            }
        }
    }

    private void readFile(File java, Counter<String> counter) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(java));
        String line;
        while ((line = reader.readLine()) != null) {
            for (String key : notRuleKey) {
                int index = 0;
                while ((line.indexOf("\"" + key + "\"", index)) != -1) {
                    index++;
                    counter.add(key);
                }
            }
        }
    }
}
