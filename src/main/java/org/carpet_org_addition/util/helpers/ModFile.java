package org.carpet_org_addition.util.helpers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.carpet_org_addition.CarpetOrgAddition;

import java.io.File;
import java.nio.file.Path;

public class ModFile {
    private final File modFileDirectory;

    @SuppressWarnings("SpellCheckingInspection")
    public ModFile(MinecraftServer server, String... args) {
        Path path = server.getSavePath(WorldSavePath.ROOT).resolve("carpetorgaddition");
        for (String name : args) {
            path = path.resolve(name);
        }
        this.modFileDirectory = path.toFile();
        if (this.modFileDirectory.isDirectory() || this.modFileDirectory.mkdirs()) {
            return;
        }
        CarpetOrgAddition.LOGGER.warn(this.modFileDirectory + "文件夹创建失败");
    }

    public File getModFile(String fileName) {
        return new File(this.modFileDirectory, fileName);
    }
}
