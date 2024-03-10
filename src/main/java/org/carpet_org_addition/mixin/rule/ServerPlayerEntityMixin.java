package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    private final ServerPlayerEntity thisPlayer = (ServerPlayerEntity) (Object) this;

    // 玩家被闪电苦力怕炸死掉落头颅
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void dropHead(DamageSource damageSource, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.playerDropHead
                && damageSource.getAttacker() instanceof CreeperEntity creeperEntity
                && creeperEntity.shouldDropHead()) {
            ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
            NbtCompound nbt = new NbtCompound();
            nbt.putString("SkullOwner", thisPlayer.getName().getString());
            itemStack.setNbt(nbt);
            creeperEntity.onHeadDropped();
            thisPlayer.dropStack(itemStack);
        }
    }
}
