package org.docs.rule;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import org.carpetorgaddition.CarpetOrgAdditionSettings;

import java.lang.reflect.Field;
import java.util.StringJoiner;

class RuleInformation {
    private final Field field;
    private final String name;
    private final String docs;
    private final String[] extra;
    private final String[] categories;
    private final String[] options;

    RuleInformation(Field field, String name, String docs, String[] extra) {
        this.field = field;
        this.name = name;
        this.docs = docs;
        this.extra = extra;
        Rule annotation = field.getAnnotation(Rule.class);
        if (annotation == null) {
            throw new IllegalArgumentException();
        }
        this.categories = annotation.categories();
        this.options = annotation.options();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // 规则名称
        sb.append("### ").append(this.name).append("(").append(this.field.getName()).append(")\n");
        sb.append("\n");
        // 规则描述
        sb.append(this.docs).append(this.hasExtra() ? "<br/>\n" : "\n");
        // 规则扩展描述
        if (this.hasExtra()) {
            for (String extra : this.extra) {
                sb.append("_").append(extra).append("_<br/>\n");
            }
            sb.append("\n");
        } else {
            sb.append("\n");
        }
        // 参数类型
        sb.append("- 类型：`").append(this.getArgumentType()).append("`\n");
        // 参数默认值
        sb.append("- 默认值：`").append(this.getDefaultValue()).append("`\n");
        // 参考选项
        if (this.options.length > 0) {
            StringJoiner sj = new StringJoiner("，", "- 参考选项：", "\n");
            for (String option : this.options) {
                sj.add("`" + option + "`");
            }
            sb.append(sj);
        } else if (isBoolean()) {
            sb.append("- 参考选项：`true`，`false`\n");
        }
        // 分类
        sb.append(this.getCategory());
        return sb.toString();
    }

    // 规则值是否是布尔类型
    private boolean isBoolean() {
        return boolean.class.isAssignableFrom(this.field.getType()) || Boolean.class.isAssignableFrom(this.field.getType());
    }

    // 规则是否有扩展描述
    private boolean hasExtra() {
        return this.extra.length > 0;
    }

    // 获取参数类型名称
    private String getArgumentType() {
        Class<?> type = this.field.getType();
        if (String.class.isAssignableFrom(type)) {
            return "字符串";
        }
        if (byte.class.isAssignableFrom(type) || short.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)
                || Byte.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return "整数";
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return "长整数";
        }
        if (float.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)
                || Float.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return "小数";
        }
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return "布尔值";
        }
        if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            return "字符";
        }
        if (Enum.class.isAssignableFrom(type)) {
            return "枚举";
        }
        throw new RuntimeException(this.field.getType().getName());
    }

    // 获取规则默认值
    private String getDefaultValue() {
        try {
            return this.field.get(null).toString().toLowerCase();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // 获取规则分类
    private String getCategory() {
        StringJoiner stringJoiner = new StringJoiner("，", "- 分类：", "\n");
        for (String category : this.categories) {
            stringJoiner.add("`" + switch (category) {
                case CarpetOrgAdditionSettings.ORG -> "Org";
                case RuleCategory.BUGFIX -> "漏洞修复";
                case RuleCategory.SURVIVAL -> "生存";
                case RuleCategory.CREATIVE -> "创造";
                case RuleCategory.EXPERIMENTAL -> "试验性";
                case RuleCategory.OPTIMIZATION -> "优化";
                case RuleCategory.FEATURE -> "特性";
                case RuleCategory.COMMAND -> "命令";
                case RuleCategory.TNT -> "TNT";
                case RuleCategory.DISPENSER -> "发射器";
                case RuleCategory.SCARPET -> "Scarpet脚本语言";
                case RuleCategory.CLIENT -> "客户端";
                default -> category;
            } + "`");
        }
        return stringJoiner.toString();
    }
}
