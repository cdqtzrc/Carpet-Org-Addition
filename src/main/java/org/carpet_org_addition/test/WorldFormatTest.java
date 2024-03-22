package org.carpet_org_addition.test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.carpet_org_addition.util.helpers.WorldFormat;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

public class WorldFormatTest {

    /**
     * 测试{@link Gson#fromJson(Reader, Type)}是否会自动关闭流
     */
    @Test
    public void WhetherToClose() throws IOException {
        File file = new File("src/main/resources/assets/carpet-org-addition/test.json");
        Gson gson = WorldFormat.createGson();
        BufferedReader reader = WorldFormat.toReader(file);
        gson.fromJson(reader, JsonObject.class);
        reader.close();
        String line;
        // 应该不会自动关闭
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Test
    public void copyFolderTest() throws IOException {
        File from = new File("src/main/resources");
        File to = new File("src/copy/resources");
        WorldFormat.copyFolder(from, to);
    }
}
