package org.carpet_org_addition.mixin.rule;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.villagerinventory.VillagerContainerScreenHandler;
import org.carpet_org_addition.util.villagerinventory.VillagerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//村民立即补货
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    @Shadow
    public abstract ActionResult interactMob(PlayerEntity player, Hand hand);

    private final VillagerEntity thisVillager = (VillagerEntity) (Object) this;

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    //每次交互都会先触发一次补货
    @Inject(method = "interactMob", at = @At(value = "HEAD"))
    private void forceRestock(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (CarpetOrgAdditionSettings.villagerImmediatelyRestock) {
            for (TradeOffer tradeOffer : thisVillager.getOffers()) {
                tradeOffer.resetUses();
            }
        }
    }

    //阻止村民变成女巫
    @Inject(method = "onStruckByLightning", at = @At("HEAD"), cancellable = true)
    private void onStruckByLightning(ServerWorld world, LightningEntity lightning, CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.disableVillagerWitch) {
            super.onStruckByLightning(world, lightning);
            ci.cancel();
        }
    }


    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void clearVillagerInventory(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        //打开村民物品栏
        if (CarpetOrgAdditionSettings.openVillagerInventory && player.isSneaking()) {
            SimpleNamedScreenHandlerFactory screen =
                    new SimpleNamedScreenHandlerFactory((i, inventory, playerEntity)
                            -> new VillagerContainerScreenHandler(i, inventory,
                            new VillagerInventory(thisVillager)), thisVillager.getName());
            player.openHandledScreen(screen);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    //村民回血
    @Inject(method = "mobTick", at = @At("HEAD"))
    private void heal(CallbackInfo ci) {
        if (CarpetOrgAdditionSettings.villagerHeal) {
            long worldTime = thisVillager.getWorld().getTime();
            if (worldTime % 80 == 0) {
                thisVillager.heal(1.0F);
            }
        }
    }
}
