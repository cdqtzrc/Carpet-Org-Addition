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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.MessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.PlayerEnderChestScreenHandler;
import org.carpet_org_addition.util.fakeplayer.PlayerInventoryScreenHandler;

@SuppressWarnings("SameReturnValue")
public class PlayerToolsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("playerTools")
                .requires(source -> CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerTools))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.literal("enderChest")
                                .executes(context -> openEnderChest(context, CommandUtils.getArgumentPlayer(context))))
                        .then(CommandManager.literal("inventory")
                                .executes(context -> openFakePlayerInventory(context, CommandUtils.getArgumentPlayer(context))))
                        .then(CommandManager.literal("teleport")
                                .executes(context -> fakePlayerTp(context, CommandUtils.getArgumentPlayer(context))))
                        .then(CommandManager.literal("isFakePlayer")
                                .executes(context -> isFakePlayer(context, CommandUtils.getArgumentPlayer(context))))
                        .then(CommandManager.literal("position")
                                .executes(context -> getFakePlayerPos(context, CommandUtils.getArgumentPlayer(context))))
                        .then(CommandManager.literal("heal")
                                .executes(context -> fakePlayerHeal(context, CommandUtils.getArgumentPlayer(context))))));
    }

    //假玩家治疗
    private static int fakePlayerHeal(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            float health = fakePlayer.getMaxHealth() - fakePlayer.getHealth();
            fakePlayer.heal(fakePlayer.getMaxHealth());
            fakePlayer.getHungerManager().setFoodLevel(20);
            //发送血量回复完后的命令反馈
            MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.playerTools.heal", fakePlayer.getDisplayName());
            return (int) health;
        }
        return 0;
    }

    //打开玩家末影箱
    private static int openEnderChest(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        PlayerEntity player = CommandUtils.getSourcePlayer(context);
        //检查玩家是否是假玩家或自己
        if (fakePlayer instanceof EntityPlayerMPFake || fakePlayer == player) {
            //创建GUI对象
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, inventory, playerEntity1) ->
                    new PlayerEnderChestScreenHandler(i, inventory, fakePlayer), fakePlayer.getName());
            //打开末影箱GUI
            player.openHandledScreen(screen);
        } else {
            //只允许操作自己和假玩家
            throw CommandUtils.createException("carpet.commands.playerTools.self_or_fake_player");
        }
        return 1;
    }

    //假玩家传送
    private static int fakePlayerTp(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        //获取假玩家名和命令执行玩家名
        Text fakePlayerName = fakePlayer.getDisplayName();
        Text playerName = player.getDisplayName();
        //在假玩家位置播放潜影贝传送音效
        fakePlayer.getWorld().playSound(null, fakePlayer.prevX, fakePlayer.prevY, fakePlayer.prevZ,
                SoundEvents.ENTITY_SHULKER_TELEPORT, fakePlayer.getSoundCategory(), 1.0f, 1.0f);
        fakePlayer.teleport(player.getServerWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        //在聊天栏显示命令反馈
        MessageUtils.sendCommandFeedback(context.getSource(), "carpet.commands.playerTools.tp.success", fakePlayerName, playerName);
        return 1;
    }

    //判断玩家是否为假玩家
    private static int isFakePlayer(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) {
        Text playerName = fakePlayer.getDisplayName();
        ServerCommandSource source = context.getSource();
        if (fakePlayer instanceof EntityPlayerMPFake) {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.is_fake_player", playerName);
            return 0;
        } else {
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.is_player", playerName);
            return 1;
        }
    }

    //获取假玩家位置
    private static int getFakePlayerPos(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            ServerCommandSource source = context.getSource();
            MessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.pos", fakePlayer.getDisplayName(),
                    getDimensionText(fakePlayer.getWorld()).getString(),
                    TextUtils.blockPos(new BlockPos(fakePlayer.getBlockPos()), Formatting.GREEN));
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
        ServerPlayerEntity player = CommandUtils.getSourcePlayer(context);
        if (CommandUtils.checkFakePlayer(fakePlayer)) {
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity)
                    -> new PlayerInventoryScreenHandler(syncId, playerInventory, fakePlayer), fakePlayer.getName());
            player.openHandledScreen(screen);
        }
        return 1;
    }
}
