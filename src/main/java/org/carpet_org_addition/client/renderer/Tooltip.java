package org.carpet_org_addition.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

@SuppressWarnings("unused")
public class Tooltip {
    private Tooltip() {
    }

    /**
     * 在屏幕中心偏右下的位置渲染一个提示框
     *
     * @param context 绘制上下文
     * @param width   游戏窗口宽度
     * @param height  游戏窗口高度
     * @param list    提示框的内容，一个元素表示提示内的一行文本
     */
    public static void drawTooltip(DrawContext context, int width, int height, List<Text> list) {
        MinecraftClient client = MinecraftClient.getInstance();
        context.drawTooltip(client.textRenderer, list, width / 2 + 7, height / 2 + 27);
    }
}
