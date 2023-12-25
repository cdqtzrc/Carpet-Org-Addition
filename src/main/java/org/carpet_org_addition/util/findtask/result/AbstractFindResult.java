package org.carpet_org_addition.util.findtask.result;

import net.minecraft.text.MutableText;

/**
 * /finder命令的查找结果
 */
public abstract class AbstractFindResult {
    /**
     * 用来在发送反馈时显示每一条查找结果的消息
     *
     * @return 将查找结果转换为可变文本对象用来在游戏中显示
     */
    public abstract MutableText toText();
}
