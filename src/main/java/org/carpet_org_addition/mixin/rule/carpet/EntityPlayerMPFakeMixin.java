package org.carpet_org_addition.mixin.rule.carpet;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.carpet_org_addition.CarpetOrgAddition;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.*;
import org.carpet_org_addition.util.helpers.ItemMatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin extends ServerPlayerEntity implements FakePlayerActionInterface, FakePlayerProtectInterface {
    @Unique
    private final EntityPlayerMPFake thisPlayer = (EntityPlayerMPFake) (Object) this;
    //用来决定假人的操作类型
    @Unique
    private FakePlayerActionType action = FakePlayerActionType.STOP;
    //假玩家操作类型的命令参数
    @Unique
    private CommandContext<ServerCommandSource> context = null;
    //假玩家保护类型
    @Unique
    private FakePlayerProtectType protect = FakePlayerProtectType.NONE;

    //私有化构造方法，防止被创建对象
    private EntityPlayerMPFakeMixin(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
        super(server, world, profile, clientOptions);
    }

    //命令上下文
    @Override
    public CommandContext<ServerCommandSource> getContext() {
        return context;
    }

    @Override
    public void setContext(CommandContext<ServerCommandSource> context) {
        this.context = context;
    }

    //假玩家操作类型
    @Override
    public FakePlayerActionType getAction() {
        return action;
    }

    @Override
    public void setAction(FakePlayerActionType action) {
        this.action = action;
    }

    // 假玩家3x3合成时的配方
    @Override
    public ItemMatcher[] get3x3Craft() {
        return ITEMS_3X3;
    }

    @Override
    public void set3x3Craft(ItemMatcher[] items) {
        // 数组拷贝
        System.arraycopy(items, 0, ITEMS_3X3, 0, ITEMS_3X3.length);
    }

    @Override
    public ItemMatcher[] get2x2Craft() {
        return ITEMS_2X2;
    }

    @Override
    public void set2x2Craft(ItemMatcher[] items) {
        System.arraycopy(items, 0, ITEMS_2X2, 0, ITEMS_2X2.length);
    }

    //假玩家保护类型
    @Override
    public FakePlayerProtectType getProtect() {
        return protect;
    }

    @Override
    public void setProtected(FakePlayerProtectType protect) {
        this.protect = protect;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void fakePlayerTick(CallbackInfo ci) {
        if (thisPlayer == null) {
            return;
        }
        //假玩家回血
        if (CarpetOrgAdditionSettings.fakePlayerHeal) {
            long time = thisPlayer.getWorld().getTime();
            if (time % 40 == 0) {
                thisPlayer.heal(1);
            }
        }
        try {
            //根据假玩家操作类型执行操作
            if (action != FakePlayerActionType.STOP && context != null) {
                this.fakePlayerAction();
            }
        } catch (RuntimeException e) {
            //将错误信息写入日志
            CarpetOrgAddition.LOGGER.error(thisPlayer.getName().getString() + "在执行操作“" + this.action.toString() + "”时遇到意外错误:", e);
            //让假玩家停止当前操作
            this.action = FakePlayerActionType.STOP;
            //向聊天栏发送错误消息的反馈
            MessageUtils.broadcastTextMessage(thisPlayer,
                    TextUtils.getTranslate("carpet.commands.playerAction.exception.runtime",
                            thisPlayer.getDisplayName()));
        }
    }

    //根据假玩家操作类型执行操作
    @Unique
    private void fakePlayerAction() {
        switch (action) {
            // 假玩家分拣
            case SORTING -> FakePlayerSorting.sorting(context, thisPlayer);
            // 假玩家清空容器
            case CLEAN -> FakePlayerClean.clean(thisPlayer);
            // 假玩家填充容器
            case FILL -> FakePlayerMoveItem.moveItem(context, thisPlayer);
            // 假玩家自动合成物品（单个材料）
            case CRAFT_ONE -> FakePlayerCraft.craftOne(context, thisPlayer, ITEMS_2X2);
            // 假玩家自动合成物品（四个相同的材料）
            case CRAFT_FOUR -> FakePlayerCraft.craftFour(context, thisPlayer, ITEMS_2X2);
            // 假玩家自动合成物品（九个相同的材料）
            case CRAFT_NINE -> FakePlayerCraft.craftNine(context, thisPlayer, ITEMS_3X3);
            // 假玩家自动合成物品（9x9自定义物品）
            case CRAFT_3X3 -> FakePlayerCraft.craft3x3(context, thisPlayer, ITEMS_3X3);
            // 假玩家自动合成物品（4x4自定义物品）
            case CRAFT_2X2 -> FakePlayerCraft.craft2x2(context, thisPlayer, ITEMS_2X2);
            // 假玩家自动重命名
            case RENAME -> FakePlayerRename.rename(context, thisPlayer);
            // 假玩家切石机
            case STONECUTTING -> FakePlayerStonecutting.stonecutting(context, thisPlayer);
            // 假玩家交易
            case TRADE -> FakePlayerTrade.trade(context, thisPlayer);
            // 以上值都不匹配，设置操作类型为STOP（不应该出现都不匹配的情况）
            default -> {
                CarpetOrgAddition.LOGGER.error(action + "的行为没有预先定义");
                action = FakePlayerActionType.STOP;
            }
        }
    }

    //阻止受保护的假玩家受到伤害
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (FakePlayerProtectManager.isNotDamage(thisPlayer) && !(source.getSource() instanceof PlayerEntity)
                && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return super.damage(source, amount);
    }

    //阻止受保护的假玩家死亡
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeath(DamageSource source, CallbackInfo ci) {
        if (FakePlayerProtectManager.isNotDeath(thisPlayer) && !(source.getSource() instanceof PlayerEntity)
                && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.setHealth(this.getMaxHealth());
            HungerManager hungerManager = this.getHungerManager();
            hungerManager.setFoodLevel(20);
            hungerManager.setSaturationLevel(5.0f);
            hungerManager.setExhaustion(0);
            ci.cancel();
        }
    }
}
