package org.carpetorgaddition.mixin.rule;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.carpetorgaddition.util.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

//强化引雷
@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    private TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ItemStack getWeaponStack();

    // 击中实体
    @WrapOperation(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/item/ItemStack;Ljava/util/function/Consumer;)V"))
    private void onEnhityHit(ServerWorld world, Entity target, DamageSource damageSource, ItemStack weapon, Consumer<Item> breakCallback, Operation<Void> original) {
        original.call(world, target, damageSource, weapon, breakCallback);
        spwnLighining(world, target.getBlockPos());
    }

    // 击中避雷针
    @Inject(method = "onBlockHitEnchantmentEffects", at = @At(value = "TAIL"))
    private void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack, CallbackInfo ci) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (world.getBlockState(blockPos).isOf(Blocks.LIGHTNING_ROD)) {
            spwnLighining(world, blockPos.up());
        }
    }

    // 生成闪电
    @Unique
    private void spwnLighining(ServerWorld world, BlockPos blockPos) {
        // 只需要在晴天生成，因为雷雨天的引雷三叉戟本来就会生成闪电
        if (world.isRaining() && world.isThundering()) {
            return;
        }
        boolean hasChanneling = EnchantmentUtils.hasEnchantment(world, Enchantments.CHANNELING, this.getWeaponStack());
        if (CarpetOrgAdditionSettings.channelingIgnoreWeather && World.isValid(blockPos) && hasChanneling) {
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.spawn(world, blockPos, SpawnReason.TRIGGERED);
            if (lightning == null) {
                return;
            }
            if (this.getOwner() instanceof ServerPlayerEntity player) {
                lightning.setChanneler(player);
            }
        }
    }
}
