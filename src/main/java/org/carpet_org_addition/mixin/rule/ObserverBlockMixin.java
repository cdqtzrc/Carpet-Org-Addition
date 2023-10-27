package org.carpet_org_addition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin extends FacingBlock {
    @Shadow
    protected abstract void scheduleTick(WorldAccess world, BlockPos pos);

    private ObserverBlockMixin(Settings settings) {
        super(settings);
    }

    //可激活侦测器，打火石右键激活
    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (CarpetOrgAdditionSettings.canActivatesObserver) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.FLINT_AND_STEEL) && !player.isSneaking()) {
                this.scheduleTick(world, pos);
                itemStack.damage(1, player, player1 -> player1.sendToolBreakStatus(hand));
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1, 1);
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
