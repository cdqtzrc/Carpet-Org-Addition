package org.carpet_org_addition.command;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.FakePlayerEnderChestScreenHandler;
import org.carpet_org_addition.util.fakeplayer.FakePlayerInventoryScreenHandler;
import org.carpet_org_addition.util.fakeplayer.FakePlayerProtectManager;

@SuppressWarnings("SameReturnValue")
public class PlayerToolsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("playerTools").requires(source ->
                        CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerTools))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.literal("enderChest").executes(context -> openEnderChest(context.getSource(), CommandUtils.getPlayerEntity(context))))
                        .then(CommandManager.literal("inventory").executes(context -> openFakePlayerInventory(context, CommandUtils.getPlayerEntity(context))))
                        .then(CommandManager.literal("teleport").executes(context -> fakePlayerTp(context.getSource(), CommandUtils.getPlayerEntity(context))))
                        .then(CommandManager.literal("isFakePlayer").executes(context -> isFakePlayer(context.getSource(), CommandUtils.getPlayerEntity(context))))
                        .then(CommandManager.literal("position").executes(context -> getFakePlayerPos(context.getSource(), CommandUtils.getPlayerEntity(context))))
                        .then(CommandManager.literal("heal").executes(context -> fakePlayerHeal(context.getSource(), CommandUtils.getPlayerEntity(context))))
                ));
    }

    //假玩家治疗
    private static int fakePlayerHeal(ServerCommandSource source, PlayerEntity fakePlayer) throws CommandSyntaxException {
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            float health = fakePlayer.getMaxHealth() - fakePlayer.getHealth();
            fakePlayer.heal(fakePlayer.getMaxHealth());
            fakePlayer.getHungerManager().setFoodLevel(20);
            //发送血量回复完后的命令反馈
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.heal", fakePlayer.getDisplayName());
            return (int) health;
        }
        return 0;
    }

    //打开玩家末影箱
    private static int openEnderChest(ServerCommandSource source, ServerPlayerEntity playerEntity) throws CommandSyntaxException {
        PlayerEntity player = CommandUtils.getPlayer(source);
        //检查玩家是否是假玩家或自己
        if (playerEntity instanceof EntityPlayerMPFake || playerEntity == player) {
            //创建GUI对象
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, inventory, playerEntity1) ->
                    new FakePlayerEnderChestScreenHandler(i, inventory,
                            playerEntity.getEnderChestInventory(), playerEntity), playerEntity.getName());
            //打开末影箱GUI
            player.openHandledScreen(screen);
        } else {
            //只允许操作自己和假玩家
            throw CommandUtils.createException("carpet.commands.playerTools.self_or_fake_player");
        }
        return 1;
    }

    //假玩家传送
    private static int fakePlayerTp(ServerCommandSource source, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(source);
        //获取假玩家名和命令执行玩家名
        Text fakePlayerName = fakePlayer.getDisplayName();
        Text playerName = player.getDisplayName();
        //判断被执行的玩家是否为假玩家
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            if (FakePlayerProtectManager.isProtect((EntityPlayerMPFake) fakePlayer)) {
                //不能传送受保护的假玩家
                throw CommandUtils.createException("carpet.commands.playerTools.tp.protected_fake_player");
            }
        }
        //不需要return，程序在执行到上面的判断是否为假玩家时，只有是假玩家才能正常返回，非假玩家会直接抛出异常
        //在假玩家位置播放潜影贝传送音效
        fakePlayer.getWorld().playSound(null, fakePlayer.prevX, fakePlayer.prevY, fakePlayer.prevZ,
                SoundEvents.ENTITY_SHULKER_TELEPORT, fakePlayer.getSoundCategory(), 1.0f, 1.0f);
        fakePlayer.teleport(player.getServerWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        //在聊天栏显示命令反馈
        MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.tp.success", fakePlayerName, playerName);
        return 1;
    }

    //判断玩家是否为假玩家
    private static int isFakePlayer(ServerCommandSource source, PlayerEntity fakePlayer) {
        Text playerName = fakePlayer.getDisplayName();
        if (fakePlayer instanceof EntityPlayerMPFake) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.is_fake_player", playerName);
            return 0;
        } else {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.is_player", playerName);
            return 1;
        }
    }

    //获取假玩家位置
    private static int getFakePlayerPos(ServerCommandSource source, PlayerEntity fakePlayer) throws CommandSyntaxException {
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            MutableText text = Texts.bracketed(Text.translatable("chat.coordinates", fakePlayer.getBlockX(),
                    fakePlayer.getBlockY(), fakePlayer.getBlockZ())).styled((Style style)
                    -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                            fakePlayer.getBlockX() + " " + fakePlayer.getBlockY() + " " + fakePlayer.getBlockZ()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click"))));
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.pos", fakePlayer.getDisplayName(),
                    getDimensionText(fakePlayer.getWorld()).getString(), text);
            ServerPlayerEntity player = source.getPlayer();
            if (player != null) {
                return MathUtils.getBlockIntegerDistance(player.getBlockPos(), fakePlayer.getBlockPos());
            }
        }
        return 0;
    }

    //获取维度名称
    private static Text getDimensionText(World world) {
        Identifier value = world.getDimensionKey().getValue();
        if (value.equals(DimensionTypes.OVERWORLD_ID)) {
            return TextUtils.getTranslate("carpet.commands.playerTools.pos.overworld");
        } else if (value.equals(DimensionTypes.THE_NETHER_ID)) {
            return TextUtils.getTranslate("carpet.commands.playerTools.pos.the_nether");
        } else if (value.equals(DimensionTypes.THE_END_ID)) {
            return TextUtils.getTranslate("carpet.commands.playerTools.pos.the_end");
        }
        return TextUtils.getTranslate("carpet.commands.playerTools.pos.default");
    }


    // 打开假玩家物品栏GUI
    private static int openFakePlayerInventory(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer)
            throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity)
                    -> new FakePlayerInventoryScreenHandler(syncId, playerInventory,
                    (EntityPlayerMPFake) fakePlayer), fakePlayer.getName());
            player.openHandledScreen(screen);
        }
        return 1;
    }
}
