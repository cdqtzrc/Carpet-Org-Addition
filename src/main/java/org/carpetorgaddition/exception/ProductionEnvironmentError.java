package org.carpetorgaddition.exception;

import net.fabricmc.loader.api.FabricLoader;

public class ProductionEnvironmentError extends Error {
    public ProductionEnvironmentError() {
    }

    /**
     * 断言当前环境为开发环境
     */
    public static void assertDevelopmentEnvironment() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return;
        }
        throw new ProductionEnvironmentError();
    }
}
