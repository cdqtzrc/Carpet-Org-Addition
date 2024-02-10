package org.carpet_org_addition.mixin.util;

import net.minecraft.text.TranslatableTextContent;
import org.carpet_org_addition.translate.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 为{@link TranslatableText}重写私有方法
 */
@SuppressWarnings("GrazieInspection")
@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin {
/*    @Shadow
    protected abstract void forEachPart(String translation, Consumer<StringVisitable> partsConsumer);

    @Shadow
    private List<StringVisitable> translations;

    @Shadow
    private Language languageCache;

    @Shadow
    @Final
    private String key;

    @Inject(method = "updateTranslations", at = @At("HEAD"), cancellable = true)
    private void updateTranslations(CallbackInfo ci) {
        if ((Object) this instanceof TranslatableText translatableText) {
            try {
                Language language = Language.getInstance();
                if (language == this.languageCache) {
                    return;
                }
                this.languageCache = language;
                String string = language.get(this.key, translatableText.getFallback());
                if (string == null) {
                    if (translatableText.getFallback() == null) {
                        try {
                            string = Objects.requireNonNull(Translate.getTranslate()).get(translatableText.getKey());
                        } catch (NullPointerException e) {
                            CarpetOrgAddition.LOGGER.warn("获取不到翻译键：" + translatableText + "，语言：" + CarpetSettings.language, e);
                            string = null;
                        }
                        if (string == null) {
                            string = translatableText.getKey();
                        }
                    } else {
                        string = translatableText.getFallback();
                    }
                }
                try {
                    ImmutableList.Builder<StringVisitable> builder = ImmutableList.builder();
                    this.forEachPart(string, builder::add);
                    this.translations = builder.build();
                } catch (TranslationException translationException) {
                    this.translations = ImmutableList.of(StringVisitable.plain(string));
                }
            } finally {
                ci.cancel();
            }
        }
}*/
}
