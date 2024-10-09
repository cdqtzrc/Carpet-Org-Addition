package org.carpetorgaddition;

import org.carpetorgaddition.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class EmptyFileTest {
    // 检查是否有空文件
    @Test
    public void hasEmptyFile() throws IOException {
        File file = new File("src/main/java/org/carpetorgaddition");
        this.traverse(file);
    }

    // 遍历文件夹
    private void traverse(File file) throws IOException {
        // 如果是文件，检查类文件是否被声明，否则遍历文件夹
        if (file.isFile()) {
            Assert.assertFalse("空文件：" + file.getPath(), this.isEmptyFile(file));
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            // 递归遍历文件
            for (File each : files) {
                this.traverse(each);
            }
        }
    }

    // 检查是不是空文件
    private boolean isEmptyFile(File file) throws IOException {
        try (BufferedReader reader = IOUtils.toReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 空行
                if (line.isBlank()) {
                    continue;
                }
                // 纯注释内容
                if (line.strip().startsWith("//") || line.strip().startsWith("/*")) {
                    continue;
                }
                // 文件有内容
                return false;
            }
        }
        return true;
    }
}
