package org.carpet_org_addition.command;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;

@Deprecated
public class EnderChestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("enderchest").requires(source ->
                                CommandHelper.canUseCommand(source, /*CarpetOrgSetting.commandEnderChest*/false)).executes(context -> {
                            //打开玩家末影箱
                            openEnderChest(context.getSource(), Collections.singleton(context.getSource().getPlayer()));
                            return 1;
                        })
                        .then(CommandManager.argument("player", EntityArgumentType.players()).executes(context -> {
                            //打开玩家末影箱
                            openEnderChest(context.getSource(), EntityArgumentType.getPlayers(context, "player"));
                            return 1;
                        }))
        );
    }

    //打开玩家末影箱
    private static void openEnderChest(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        PlayerEntity player = source.getPlayer();
        if (player == null) {
            return;
        }
        int size = targets.size();
        if (size != 1) {
            player.sendMessage(Text.literal("只允许同时操作一个玩家，但是目标中包含" + size + "个玩家"));
            return;
        }
        //从集合中获取玩家
        for (PlayerEntity playerEntity : targets) {
            //检查玩家是否是假玩家或自己
            if (playerEntity instanceof EntityPlayerMPFake || player == playerEntity) {
                //创建GUI对象
                SimpleNamedScreenHandlerFactory screen = new SimpleNamedScreenHandlerFactory((i, inventory, playerEntity1) ->
                        GenericContainerScreenHandler.createGeneric9x3(i, inventory, playerEntity.getEnderChestInventory()), playerEntity.getName());
                //打开末影箱GUI
                player.openHandledScreen(screen);
            } else {
                player.sendMessage(Text.literal("只允许操作自己和假玩家"));
            }
        }
    }
}
