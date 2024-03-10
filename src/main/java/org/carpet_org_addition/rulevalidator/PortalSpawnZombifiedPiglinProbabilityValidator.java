package org.carpet_org_addition.rulevalidator;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;
import org.carpet_org_addition.util.TextUtils;
import org.jetbrains.annotations.Nullable;

public class PortalSpawnZombifiedPiglinProbabilityValidator extends Validator<Integer> {
    private PortalSpawnZombifiedPiglinProbabilityValidator() {
    }

    /**
     * 下界传送门方块接收到随机刻后，会把一个0-1999之间的随机数与游戏难度的id进行比较，如果随机数小于难度id，就会尝试生成一只僵尸猪灵，在这里，我们把游戏难度的id进行重定向为我们自己输入的数值，游戏在决定是否生成僵尸猪灵时，会比较我们自己设定的值而不是游戏难度id，这样就可以通过控制生成僵尸猪灵的概率来改变主世界猪人塔的效率，当然我们也可以设置为0来禁止僵尸猪灵生成<br/>
     * 由于随机数的范围是0-1999，因此设置值可以等于2000没有实际意义，但是为了方便，我们仍然规定值的取值范围是0-2000，小于0的值同样没有意义，所以我们规定-1表示原版的默认值
     */
    @Override
    public Integer validate(@Nullable ServerCommandSource serverCommandSource, CarpetRule<Integer> carpetRule, Integer integer, String s) {
        return (integer >= 0 && integer <= 2000) || integer == -1 ? integer : null;
    }

    /**
     * 输入的下界传送门方块生成僵尸猪灵概率的值为非法参数时显示的信息
     */
    @Override
    public String description() {
        return TextUtils.getTranslate("carpet.rule.validate.portalSpawnZombifiedPiglinProbability").getString();
    }
}
