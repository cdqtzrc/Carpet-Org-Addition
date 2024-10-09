package org.carpetorgaddition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

//可采集刷怪笼
@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BlockWithEntity {
    protected SpawnerBlockMixin(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "onStacksDropped", at = @At("HEAD"), cancellable = true)
    // 使用精准采集工具挖掘时不会掉落经验
    private void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.canMineSpawner && EnchantmentHelper.hasSilkTouch(tool)) {
            super.onStacksDropped(state, world, pos, tool, dropExperience);
            ci.cancel();
        }
    }

    @Override
    // 使用精准采集挖掘时掉落带NBT的物品
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (CarpetOrgAdditionSettings.canMineSpawner && !player.isCreative() && EnchantmentHelper.hasSilkTouch(player.getMainHandStack())) {
            if (world.getBlockEntity(pos) instanceof MobSpawnerBlockEntity mobSpawnerBlock) {
                ItemStack itemStack = new ItemStack(Items.SPAWNER);
                mobSpawnerBlock.setStackNbt(itemStack);
                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
    }

    @Override
    // 放置刷怪笼时读取NBT
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient) {
            return;
        }
        if (CarpetOrgAdditionSettings.canMineSpawner && placer instanceof PlayerEntity player && !player.isCreative()) {
            NbtCompound nbt;
            try {
                nbt = Objects.requireNonNull(itemStack.getNbt()).getCompound("BlockEntityTag");
            } catch (NullPointerException e) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                blockEntity.readNbt(nbt);
            }
        }
    }
}
