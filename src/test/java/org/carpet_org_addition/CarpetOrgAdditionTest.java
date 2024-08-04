package org.carpet_org_addition;

import org.junit.Assert;
import org.junit.Test;

public class CarpetOrgAdditionTest {
    /**
     * 检查构建时Java的版本，构建时Java版本应等于Minecraft支持的最低Java版本
     */
    @Test
    public void checkJavaVersion() {
        Assert.assertEquals("请使用jdk17构建", 17, Runtime.version().version().get(0).intValue());
    }
}
