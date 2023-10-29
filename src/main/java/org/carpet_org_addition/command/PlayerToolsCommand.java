package org.carpet_org_addition.command;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.carpet_org_addition.CarpetOrgAdditionSettings;
import org.carpet_org_addition.util.CommandUtils;
import org.carpet_org_addition.util.MathUtils;
import org.carpet_org_addition.util.SendMessageUtils;
import org.carpet_org_addition.util.TextUtils;
import org.carpet_org_addition.util.fakeplayer.*;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("SameReturnValue")
public class PlayerToolsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        dispatcher.register(CommandManager.literal("playerTools").requires(source ->
                        CommandHelper.canUseCommand(source, CarpetOrgAdditionSettings.commandPlayerTools))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.literal("enderChest").executes(context -> openEnderChest(context.getSource(), EntityArgumentType.getPlayer(context, "player"))))
                        .then(CommandManager.literal("teleport").executes(context -> fakePlayerTp(context.getSource(), EntityArgumentType.getPlayer(context, "player"))))
                        .then(CommandManager.literal("isFakePlayer").executes(context -> isFakePlayer(context.getSource(), EntityArgumentType.getPlayer(context, "player"))))
                        .then(CommandManager.literal("position").executes(context -> getFakePlayerPos(context.getSource(), EntityArgumentType.getPlayer(context, "player"))))
                        .then(CommandManager.literal("heal").executes(context -> fakePlayerHeal(context.getSource(), EntityArgumentType.getPlayer(context, "player"))))
                        .then(CommandManager.literal("action").then(CommandManager
                                        .literal("sorting").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).then(CommandManager.argument("this", Vec3ArgumentType.vec3()).then(CommandManager.argument("other", Vec3ArgumentType.vec3()).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.SORTING))))))
                                .then(CommandManager.literal("clean").executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.CLEAN))).then(CommandManager.literal("fill").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.FILL)))).then(CommandManager.literal("stop").executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.STOP)))
                                .then(CommandManager.literal("craft")
                                        .then(CommandManager.literal("one").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.CRAFT_ONE))))
                                        .then(CommandManager.literal("nine").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.CRAFT_NINE))))
                                        .then(CommandManager.literal("four").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.CRAFT_FOUR))))
                                        .then(CommandManager.literal("3x3")
                                                .then(CommandManager.argument("item1", ItemStackArgumentType.itemStack(commandBuildContext))
                                                        .then(CommandManager.argument("item2", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                .then(CommandManager.argument("item3", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                        .then(CommandManager.argument("item4", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                .then(CommandManager.argument("item5", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                        .then(CommandManager.argument("item6", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                                .then(CommandManager.argument("item7", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                                        .then(CommandManager.argument("item8", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                                                .then(CommandManager.argument("item9", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                                                        .executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.CRAFT_3X3))
                                                                                                                ))))))))))
                                        .then(CommandManager.literal("2x2")
                                                .then(CommandManager.argument("item1", ItemStackArgumentType.itemStack(commandBuildContext))
                                                        .then(CommandManager.argument("item2", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                .then(CommandManager.argument("item3", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                        .then(CommandManager.argument("item4", ItemStackArgumentType.itemStack(commandBuildContext))
                                                                                .executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.CRAFT_2X2)))))))
                                        .then(CommandManager.literal("gui").executes(context -> openFakePlayerCraftGui(context, EntityArgumentType.getPlayer(context, "player")))))
                                .then(CommandManager.literal("trade").then(CommandManager.argument("index", IntegerArgumentType.integer(1))
                                        .executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.TRADE))))
                                .then(CommandManager.literal("query").executes(context -> getAction(context, EntityArgumentType.getPlayer(context, "player"))))
                                .then(CommandManager.literal("rename").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).then(CommandManager.argument("name", StringArgumentType.string()).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.RENAME)))))
                                .then(CommandManager.literal("stonecutting").then(CommandManager.argument("item", ItemStackArgumentType.itemStack(commandBuildContext)).then(CommandManager.argument("button", IntegerArgumentType.integer(1)).executes(context -> setAction(context, EntityArgumentType.getPlayer(context, "player"), FakePlayerActionType.STONE_CUTTING)))))
                        )
                ));
    }

    //假玩家治疗
    private static int fakePlayerHeal(ServerCommandSource source, PlayerEntity fakePlayer) throws CommandSyntaxException {
        if (isFakePlayer(fakePlayer)) {
            float health = fakePlayer.getMaxHealth() - fakePlayer.getHealth();
            fakePlayer.heal(fakePlayer.getMaxHealth());
            fakePlayer.getHungerManager().setFoodLevel(20);
            //发送血量回复完后的命令反馈
            SendMessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.heal", fakePlayer.getDisplayName());
            return (int) health;
        }
        return 0;
    }

    //打开玩家末影箱
    private static int openEnderChest(ServerCommandSource source, PlayerEntity playerEntity) throws CommandSyntaxException {
        PlayerEntity player = CommandUtils.getPlayer(source);
        //检查玩家是否是假玩家或自己
        if (playerEntity instanceof EntityPlayerMPFake || playerEntity == player) {
            //创建GUI对象
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, inventory, playerEntity1) ->
                    FakePlayerEnderChestScreenHandler.getFakePlayerEnderChestScreenHandler(i, inventory, playerEntity.getEnderChestInventory(), playerEntity)
                    , playerEntity.getName());
            //打开末影箱GUI
            player.openHandledScreen(screen);
        } else {
            //只允许操作自己和假玩家
            throw CommandUtils.getException("carpet.commands.playerTools.self_or_fake_player");
        }
        return 1;
    }

    //假玩家传送
    private static int fakePlayerTp(ServerCommandSource source, PlayerEntity fakePlayer) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(source);
        //获取假玩家名和命令执行玩家名
        Text fakePlayerName = fakePlayer.getDisplayName();
        Text playerName = player.getDisplayName();
        //判断被执行的玩家是否为假玩家
        if (isFakePlayer(fakePlayer)) {
            if (CarpetOrgAdditionSettings.fakePlayerProtect && FakePlayerProtectManager.isProtect((EntityPlayerMPFake) fakePlayer)) {
                //不能传送受保护的假玩家
                throw CommandUtils.getException("carpet.commands.playerTools.tp.protected_fake_player");
            }
        }
        //不需要return，程序在执行到上面的判断是否为假玩家时，只有是假玩家才能正常返回，非假玩家会直接抛出异常
        ServerWorld serverWorld;
        try {
            serverWorld = Objects.requireNonNull(player.getServer()).getWorld(player.getWorld().getRegistryKey());
        } catch (NullPointerException n) {
            return 1;
        }
        Set<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
        //在假玩家位置播放潜影贝传送音效
        fakePlayer.getWorld().playSound(null, fakePlayer.prevX, fakePlayer.prevY, fakePlayer.prevZ, SoundEvents.ENTITY_SHULKER_TELEPORT, fakePlayer.getSoundCategory(), 1.0f, 1.0f);
        //传送
        teleport(fakePlayer, serverWorld, player.getX(), player.getY(), player.getZ(), set, player.getYaw(), player.getPitch());
        //在聊天栏显示命令反馈
        SendMessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.tp.success", fakePlayerName, playerName);
        return 1;
    }

    //传送 原版/tp命令中的方法，复制粘贴过来再改一下
    private static void teleport(Entity target, ServerWorld world, double x, double y, double z, Set<PositionFlag> movementFlags, float yaw, float pitch) {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        if (World.isValid(blockPos)) {
            float f = MathHelper.wrapDegrees(yaw);
            float g = MathHelper.wrapDegrees(pitch);
            if (target.teleport(world, x, y, z, movementFlags, f, g)) {
                label23:
                {
                    if (target instanceof LivingEntity livingEntity) {
                        if (livingEntity.isFallFlying()) {
                            break label23;
                        }
                    }
                    target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
                    target.setOnGround(true);
                }
                if (target instanceof PathAwareEntity pathAwareEntity) {
                    pathAwareEntity.getNavigation().stop();
                }
            }
        }
    }

    //判断玩家是否为假玩家
    private static int isFakePlayer(ServerCommandSource source, PlayerEntity fakePlayer) {
        Text playerName = fakePlayer.getDisplayName();
        if (fakePlayer instanceof EntityPlayerMPFake) {
            SendMessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.is_fake_player", playerName);
            return 0;
        } else {
            SendMessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.is_player", playerName);
            return 1;
        }
    }

    //获取假玩家位置
    private static int getFakePlayerPos(ServerCommandSource source, PlayerEntity fakePlayer) throws CommandSyntaxException {
        if (isFakePlayer(fakePlayer)) {
            MutableText text = Texts.bracketed(Text.translatable("chat.coordinates", fakePlayer.getBlockX(), fakePlayer.getBlockY(), fakePlayer.getBlockZ())).styled((Style style) -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fakePlayer.getBlockX() + " " + fakePlayer.getBlockY() + " " + fakePlayer.getBlockZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click"))));
            SendMessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.pos", fakePlayer.getDisplayName(),
                    getDimensionText(fakePlayer.getWorld()).getString(), CarpetOrgAdditionSettings.canParseWayPoint ? text.getString() : text);
            ServerPlayerEntity player = source.getPlayer();
            if (player != null) {
                return MathUtils.getBlockIntegerDistance(player.getBlockPos(), fakePlayer.getBlockPos());
            }
        }
        return 0;
    }

    //判断是否为假玩家
    private static boolean isFakePlayer(PlayerEntity fakePlayer) throws CommandSyntaxException {
        if (fakePlayer instanceof EntityPlayerMPFake) {
            return true;
        } else {
            //不是假玩家的反馈消息
            throw CommandUtils.getException("carpet.commands.playerTools.not_fake_player", fakePlayer.getDisplayName());
        }
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

    //设置假玩家操作类型
    private static int setAction(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer, FakePlayerActionType action) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        if (action.isCraftAction() && !CarpetSettings.ctrlQCraftingFix) {
            //判断当前命令执行者是否有足够的权限
            boolean hasPermission = source.hasPermissionLevel(getCarpetPermissionLevel(source));
            MutableText suggest;
            if (hasPermission) {
                suggest = TextUtils.suggest(TextUtils.getTranslate("carpet.commands.playerTools.action.set.here").getString(),
                        "/carpet ctrlQCraftingFix true",
                        TextUtils.getTranslate("carpet.commands.playerTools.action.set.has_permission"),
                        Formatting.AQUA);
            } else {
                suggest = TextUtils.suggest(
                        TextUtils.getTranslate("carpet.commands.playerTools.action.set.here").getString(),
                        null, TextUtils.getTranslate("carpet.commands.playerTools.action.set.no_permission"),
                        Formatting.RED);
            }
            SendMessageUtils.sendCommandFeedback(source, "carpet.commands.playerTools.action.set", suggest);
        }
        //判断该玩家是否为假玩家，此处必须为假玩家，只有假玩家类实现了假玩家动作接口
        if (isFakePlayer(fakePlayer)) {
            //将假玩家类型强转为假玩家动作接口
            FakePlayerActionInterface fakePlayerActionInterface = (FakePlayerActionInterface) fakePlayer;
            //如果假玩家动作类型是3x3物品合成，为数组复制
            if (action == FakePlayerActionType.CRAFT_3X3) {
                Item[] items = new Item[9];
                for (int i = 1; i <= 9; i++) {
                    items[i - 1] = ItemStackArgumentType.getItemStackArgument(context, "item" + i).getItem();
                }
                fakePlayerActionInterface.setCraft(items);
            }
            //设置假玩家的操作类型和命令的参数
            fakePlayerActionInterface.setAction(action);
            fakePlayerActionInterface.setContext(context);
        }
        return 1;
    }

    //获取执行carpet命令需要的权限等级
    private static int getCarpetPermissionLevel(ServerCommandSource source) {
        if (CarpetOrgAdditionSettings.openCarpetPermissions && source.getServer().isSingleplayer()) {
            return 0;
        }
        if ("4".equals(CarpetSettings.carpetCommandPermissionLevel)) {
            return 4;
        }
        return 2;
    }

    //获取假玩家操作类型
    private static int getAction(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        if (isFakePlayer(fakePlayer)) {
            FakePlayerActionType action = ((FakePlayerActionInterface) fakePlayer).getAction();
            SendMessageUtils.sendTextMessage(player, TextUtils.appendAll(fakePlayer.getDisplayName(), ": ", action.getActionText(context)));
        }
        return 1;
    }

    // 打开控制假人合成物品的GUI
    private static int openFakePlayerCraftGui(CommandContext<ServerCommandSource> context, ServerPlayerEntity fakePlayer) throws CommandSyntaxException {
        ServerPlayerEntity player = CommandUtils.getPlayer(context);
        if (isFakePlayer(fakePlayer)) {
            FakePlayerActionInterface fakePlayerActionInterface = (FakePlayerActionInterface) fakePlayer;
            fakePlayerActionInterface.setContext(context);
            // 打开合成GUI
            SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity)
                    -> new FakePlayerGuiCraftScreenHandler(i, playerInventory, (EntityPlayerMPFake) fakePlayer,
                    ScreenHandlerContext.create(player.getWorld(), player.getBlockPos()), new SimpleInventory(9)), TextUtils.getTranslate("carpet.commands.playerTools.action.type.craft_gui"));
            player.openHandledScreen(screen);
        }
        return 1;
    }
}
