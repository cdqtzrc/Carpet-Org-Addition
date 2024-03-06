package org.carpet_org_addition.util.findtask.result;

import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.carpet_org_addition.command.FinderCommand;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.matcher.Matcher;

import java.math.BigDecimal;

public class ItemFindResult extends AbstractFindResult {
    /**
     * 物品所在容器的位置
     */
    private final BlockPos blockPos;
    /**
     * 物品的数量
     */
    private final int count;
    /**
     * 是否是在容器中的潜影盒中找到的
     */
    private final boolean inTheShulkerBox;
    /**
     * 容器方块名称的翻译键
     */
    private final String blockName;

    public Matcher getMatcher() {
        return matcher;
    }

    /**
     * 要查找的物品
     */
    private final Matcher matcher;

    public ItemFindResult(BlockPos blockPos, int count, boolean inTheShulkerBox, String blockName, Matcher matcher) {
        this.blockPos = blockPos;
        this.count = count;
        this.inTheShulkerBox = inTheShulkerBox;
        this.blockName = blockName;
        this.matcher = matcher;
    }

    public int getCount() {
        return this.count;
    }

    public boolean inTheShulkerBox() {
        return this.inTheShulkerBox;
    }

    @Override
    public MutableText toText() {
        String command = "/particleLine ~ ~1 ~ "
                + (BigDecimal.valueOf((double) blockPos.getX() + 0.5)) + " "
                + (BigDecimal.valueOf((double) blockPos.getY() + 0.5)) + " "
                + (BigDecimal.valueOf((double) blockPos.getZ() + 0.5));
        return TextUtils.getTranslate("carpet.commands.finder.item.each", TextUtils.blockPos(blockPos, Formatting.GREEN),
                TextUtils.command(TextUtils.getTranslate(blockName), command, null, null, true),
                matcher.isItem()
                        ? FinderCommand.showCount(matcher.getItem().getDefaultStack(), count, inTheShulkerBox)
                        : matcher.toText());
    }
}
