package org.carpet_org_addition.util.fakeplayer;

import net.minecraft.text.MutableText;
import org.carpet_org_addition.util.TextUtils;

public enum FakePlayerProtectType {
    NONE,//不受保护
    KILL,//不被kill
    DAMAGE,//不被伤害
    DEATH;//不会死亡

    public MutableText getText() {
        return switch (this) {
            case NONE -> TextUtils.getTranslate("carpet.commands.protect.type.none");
            case KILL -> TextUtils.getTranslate("carpet.commands.protect.type.kill");
            case DAMAGE -> TextUtils.getTranslate("carpet.commands.protect.type.damage");
            case DEATH -> TextUtils.getTranslate("carpet.commands.protect.type.death");
        };
    }

    //是否为受保护的类型
    public boolean isProtect() {
        return this != NONE;
    }
}
