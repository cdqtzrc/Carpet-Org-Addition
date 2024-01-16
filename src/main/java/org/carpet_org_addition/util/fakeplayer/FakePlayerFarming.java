package org.carpet_org_addition.util.fakeplayer;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.carpet_org_addition.util.WorldUtils;

import java.util.List;

public class FakePlayerFarming {
    public static void farming(EntityPlayerMPFake fakePlayer) {
        // 根据副手的物品是什么来决定种植什么农作物
        ItemStack cropsItem = fakePlayer.getOffHandStack();
        // 如果副手没有物品，直接结束方法
        if (cropsItem.isEmpty()) {
            return;
        }
        // 获取玩家所站的位置
        BlockPos blockPos = fakePlayer.getBlockPos();
        World world = fakePlayer.getWorld();
        // 获取周围水平方向扩展5格，垂直方块扩展1格的范围内所有耕地
        Box box = new Box(blockPos).expand(5, 1, 5);
        // 获取当前种植的是什么类型的农作物
        FarmingType farmingType = FarmingType.getFarmingType(cropsItem);
        switch (farmingType) {
            case CROPS -> plantingCrops(fakePlayer, box, world, cropsItem);
            case BAMBOO -> plantingBamboo(fakePlayer, box, world, cropsItem);
            case NONE -> {
            }
        }
    }

    // 种植常规的农作物，小麦、土豆、胡萝卜，甜菜，以及火把花，瓶子草
    private static void plantingCrops(EntityPlayerMPFake fakePlayer, Box box, World world, ItemStack itemStack) {
        // 获取范围内所有的耕地方块坐标
        List<BlockPos> list = WorldUtils.allBlockPos(box).stream()
                .filter(pos -> world.getBlockState(pos).isOf(Blocks.FARMLAND))
                .toList();
        for (BlockPos blockPos : list) {
            PlayerScreenHandler playerScreenHandler = fakePlayer.playerScreenHandler;
            // 玩家手上的种子太少，需要补货
            if (itemStack.getCount() <= 1) {
                DefaultedList<Slot> slots = playerScreenHandler.slots;
                if (replenishment(fakePlayer, slots.size() - 1, itemStack.getItem(), playerScreenHandler)) {
                    return;
                }
            }
            BlockPos upPos = blockPos.up();
            BlockState blockState = world.getBlockState(upPos);
            // 如果耕地上方方块是空气，种植农作物
            if (blockState.isAir()) {
                // 种植农作物
                fakePlayer.interactionManager.interactBlock(fakePlayer, world, itemStack, Hand.OFF_HAND,
                        new BlockHitResult(blockPos.toCenterPos(), Direction.UP, upPos, false));
            }
            // 种植农作物后，收集或催熟
            Block block = blockState.getBlock();
            // 处理普通的农作物
            if (block instanceof CropBlock cropBlock) {
                // 农作物已经成熟，收集农作物，火把花不能直接用isMature方法判断是否成熟
                if (cropBlock.isMature(blockState) && !(cropBlock instanceof TorchflowerBlock)) {
                    // 收集农作物（破坏方块）
                    breakBlock(fakePlayer, upPos, world);
                } else {
                    fertilize(fakePlayer, playerScreenHandler, world, upPos);
                }
            } else if (block instanceof PitcherCropBlock pitcherCropBlock) {
                // 处理瓶子草
                // 判断瓶子草是否可以施肥，如果可以，就施肥，否则瓶子草可能已经成熟，破坏瓶子草
                if (pitcherCropBlock.isFertilizable(world, upPos, blockState, false)) {
                    // 施肥
                    fertilize(fakePlayer, playerScreenHandler, world, upPos);
                } else {
                    // 收集瓶子草
                    breakBlock(fakePlayer, upPos, world);
                }
            } else if (block == Blocks.TORCHFLOWER) {
                // 收集火把花
                breakBlock(fakePlayer, upPos, world);
            }
        }
    }

    // 种植竹子
    private static void plantingBamboo(EntityPlayerMPFake fakePlayer, Box box, World world, ItemStack itemStack) {
        // 获取所有
        List<BlockPos> list = WorldUtils.allBlockPos(box).stream()
                .filter(blockPos -> world.getBlockState(blockPos).isIn(BlockTags.BAMBOO_PLANTABLE_ON)
                        // 竹子和竹笋自身也有“bamboo_plantable_on”标签，需要排除掉
                        && !world.getBlockState(blockPos).isOf(Blocks.BAMBOO)
                        && !world.getBlockState(blockPos).isOf(Blocks.BAMBOO_SAPLING))
                .filter(blockPos -> world.getBlockState(blockPos.up()).isAir()
                        || world.getBlockState(blockPos.up()).isOf(Blocks.BAMBOO)
                        || world.getBlockState(blockPos.up()).isOf(Blocks.BAMBOO_SAPLING))
                .toList();
        for (BlockPos blockPos : list) {
            PlayerScreenHandler playerScreenHandler = fakePlayer.playerScreenHandler;
            // 玩家手上的竹子太少，需要补货
            if (itemStack.getCount() <= 1) {
                DefaultedList<Slot> slots = playerScreenHandler.slots;
                if (replenishment(fakePlayer, slots.size() - 1, itemStack.getItem(), playerScreenHandler)) {
                    // 补货失败，玩家身上已经没有足够的竹子了，直接结束方法
                    return;
                }
            }
            BlockPos upPos = blockPos.up();
            BlockState blockState = world.getBlockState(upPos);
            Block block = blockState.getBlock();
            if (blockState.isAir()) {
                // 种植竹子
                fakePlayer.interactionManager.interactBlock(fakePlayer, world, itemStack, Hand.OFF_HAND,
                        new BlockHitResult(blockPos.toCenterPos(), Direction.UP, upPos, false));
            } else if (block instanceof BambooSaplingBlock) {
                // 竹笋方块，直接使用骨粉
                fertilize(fakePlayer, playerScreenHandler, world, upPos);
            } else if (block instanceof BambooBlock bambooBlock) {
                // 判断竹子是否可以施肥
                if (bambooBlock.isFertilizable(world, upPos, blockState, false)) {
                    // 可以施肥
                    // 竹子上方第一个空气方块开始，向上空气方块的数量
                    int airCount = 0;
                    // 一个标记，从这个标记变为true开始，记录上方空气的数量
                    boolean hasAir = false;
                    // 从当前竹子根的位置向上找16格，判断上方是否有上次砍伐但没来得及掉落的竹子
                    for (int i = 2; i <= 18; i++) {
                        BlockState tempBlockState = world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + i, blockPos.getZ()));
                        if (tempBlockState.isAir()) {
                            hasAir = true;
                            airCount++;
                        }
                        if (hasAir) {
                            // 如果上方连续的空气方块数量大于等于3，则可以使用骨粉
                            if (airCount >= 3) {
                                fertilize(fakePlayer, playerScreenHandler, world, upPos);
                                break;
                            } else if (!tempBlockState.isAir()) {
                                // 如果上方连续的空气方块数量小于3，不能施肥，跳出循环
                                break;
                            }
                        }
                    }
                } else {
                    // 不能施肥，破坏竹子
                    useToolBreakBlock(fakePlayer, playerScreenHandler, upPos, world, true);
                }
            }
        }
    }

    // 撒骨粉催熟
    private static void fertilize(EntityPlayerMPFake fakePlayer, PlayerScreenHandler playerScreenHandler, World world, BlockPos upPos) {
        // 农作物没有成熟，撒骨粉
        if (!fakePlayer.getMainHandStack().isOf(Items.BONE_MEAL)) {
            // 如果假玩家主手上没有骨粉，就从背包内获取骨粉
            DefaultedList<Slot> slots = playerScreenHandler.slots;
            // 从9开始是为了不从合成方格和盔甲槽寻找物品，slots.size()-1是为了不从副手上寻找物品
            for (int index = 9; index < slots.size() - 1; index++) {
                if (slots.get(index).getStack().isOf(Items.BONE_MEAL)) {
                    // 找到骨粉后，与主手的物品交换位置，然后跳出循环
                    FakePlayerUtils.swapItem(playerScreenHandler, index, fakePlayer.getInventory().selectedSlot, fakePlayer);
                    break;
                }
            }
        }
        // 如果手上有多余一个的骨粉，就使用骨粉
        if (fakePlayer.getMainHandStack().getCount() > 1) {
            // 使用骨粉
            fakePlayer.interactionManager.interactBlock(fakePlayer, world, fakePlayer.getMainHandStack(), Hand.MAIN_HAND,
                    new BlockHitResult(upPos.toCenterPos(), Direction.DOWN, upPos, true));
        } else {
            replenishment(fakePlayer, fakePlayer.getInventory().selectedSlot + 36, Items.BONE_MEAL, playerScreenHandler);
        }
    }

    // 收集农作物（需要保证方块能瞬间破坏）
    private static void breakBlock(EntityPlayerMPFake fakePlayer, BlockPos pos, World world) {
        fakePlayer.interactionManager.processBlockBreakingAction(pos,
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                Direction.DOWN, world.getHeight(), -1);
    }

    // 使用工具破坏硬度大于0的方块
    @SuppressWarnings("SameParameterValue")
    private static void useToolBreakBlock(EntityPlayerMPFake fakePlayer, PlayerScreenHandler playerScreenHandler,
                                          BlockPos pos, World world, boolean breakBamboo) {
        if (breakBamboo) {
            // 竹子应该从第二格开始破坏
            pos = pos.up();
        }
        // 如果玩家是创造模式，无需切换工具
        if (fakePlayer.isCreative()) {
            breakBlock(fakePlayer, pos, world);
        }
        // 获取应该在键盘上按下的数字键，用来与快捷栏的物品交换位置
        int numberKey = fakePlayer.getInventory().selectedSlot;
        for (int index = 9; index < playerScreenHandler.slots.size() - 1; index++) {
            if (playerScreenHandler.getSlot(index).getStack().getItem() instanceof ToolItem toolItem) {
                // 将物品栏中的工具与主手的物品交换位置
                FakePlayerUtils.swapItem(playerScreenHandler, index, numberKey, fakePlayer);
                // 剑可以瞬间破坏竹子，无需计算挖掘速度
                if ((breakBamboo && toolItem instanceof SwordItem)
                        // 判断当前工具能否瞬间破坏竹子
                        || world.getBlockState(pos).calcBlockBreakingDelta(fakePlayer, world, pos) >= 1.0) {
                    breakBlock(fakePlayer, pos, world);
                    // 工具使用完后，把工具放回原处
                    FakePlayerUtils.swapItem(playerScreenHandler, index, numberKey, fakePlayer);
                    return;
                }
                // 如果不能瞬间破坏，就把工具放回原处
                FakePlayerUtils.swapItem(playerScreenHandler, index, numberKey, fakePlayer);
            }
        }
    }

    // 自动补货
    private static boolean replenishment(EntityPlayerMPFake fakePlayer, int slotIndex, Item item, PlayerScreenHandler playerScreenHandler) {
        DefaultedList<Slot> slots = playerScreenHandler.slots;
        // 遍历玩家物品栏，找到需要的物品
        for (int index = 5; index < slots.size() - 1; index++) {
            if (index == slotIndex) {
                continue;
            }
            ItemStack itemStack = slots.get(index).getStack();
            // 找到了，就移动到指定槽位
            if (itemStack.isOf(item)) {
                // 如果物品的堆叠数已经是最大值，就移动一半，否则移动所有
                if (itemStack.getCount() == itemStack.getMaxCount()) {
                    FakePlayerUtils.pickupAndMoveHalfItemStack(playerScreenHandler, index, slotIndex, fakePlayer);
                } else {
                    FakePlayerUtils.pickupAndMoveItemStack(playerScreenHandler, index, slotIndex, fakePlayer);
                }
                return false;
            }
        }
        return true;
    }

    private enum FarmingType {
        CROPS,
        BAMBOO,
        COCOA,
        NONE;

        private static FarmingType getFarmingType(ItemStack itemStack) {
            if ((itemStack.isOf(Items.WHEAT_SEEDS)
                    || itemStack.isOf(Items.POTATO)
                    || itemStack.isOf(Items.CARROT)
                    || itemStack.isOf(Items.BEETROOT_SEEDS)
                    || itemStack.isOf(Items.TORCHFLOWER_SEEDS)
                    || itemStack.isOf(Items.PITCHER_POD))) {
                return CROPS;
            }
            if (itemStack.isOf(Items.BAMBOO)) {
                return BAMBOO;
            }
            return NONE;
        }
    }
}
