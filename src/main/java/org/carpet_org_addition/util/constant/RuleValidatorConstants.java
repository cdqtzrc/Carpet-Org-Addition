package org.carpet_org_addition.util.constant;

import net.minecraft.text.Text;
import org.carpet_org_addition.util.TextUtils;

public class RuleValidatorConstants {
    /**
     * @return 值必须大于等于number
     */
    public static Text greaterThan(int number) {
        return TextUtils.getTranslate("carpet.rule.validate.value.greater_than", number);
    }
}
