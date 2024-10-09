package org.carpetorgaddition.translate;

import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;

public class TranslatableText extends TranslatableTextContent {
    public TranslatableText(String key, @Nullable String fallback, Object[] args) {
        super(key, fallback, args);
    }
}
