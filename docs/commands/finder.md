查找玩家周围指定方块的数量

## 语法

`finder block <blockState> [<range>] [<maxCount>]`

`finder item <itemStack> [<range>] [<maxCount>]`

`finder trade item <itemStack> [<range>] [<maxCount>]`

`finder trade enchantedBook <enchantment> [<range>] [<maxCount>]`

## 参数

`blockState`:block_state

要查找的方块

`itemStack`:item_stack

要查找的物品

`range`:integer

要查找方块的范围，以玩家为中心，指定边长，高度为整个区块高度，必须介于0-128之间

`enchantment`

要查找的附魔书

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
        <td rowspan="7">任意</td>
        <td>参数不正确</td>
        <td>无法解析</td>
    </tr>
    <tr>
        <td>找到的方块过多</td>
        <td>执行失败，并在聊天栏输出找到的方块数量</td>
    </tr>
    <tr>
        <td>周围的容器过多</td>
        <td>执行失败，并在聊天栏输出周围容器的数量</td>
    </tr>
    <tr>
        <td>周围出售指定物品或附魔书的村民或流浪商人过多</td>
        <td>执行失败，并在聊天栏输出符合添加的交易的数量</td>
    </tr>
    <tr>
        <td>执行成功</td>
        <td>在聊天栏输出找到的方块数量和每一个或距离最近前几个方块的位置</td>
    </tr>
    <tr>
        <td>执行成功</td>
        <td>在聊天栏输出找到的物品数量和每一个或数量最多的前几个物品所在的容器方块的位置</td>
    </tr>
    <tr>
        <td>执行成功</td>
        <td>在聊天栏输出周围出售指定物品的村民或流浪商人的数量和位置</td>
    </tr>
</table>

## 输出

<table>
    <tr>
        <th>命令</th>
        <th>条件</th>
        <th>成功次数</th>
        <th>结果</th>
        <th>返回值</th>
    </tr>
    <tr>
      <td>任意</td>
      <td>执行失败</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>finder block ...</td>
      <td rowspan="3">执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>找到的方块数量</td>
    </tr>
    <tr>
      <td>finder item ...</td>
      <td>1</td>
      <td>1</td>
      <td>周围包含指定物品的容器的数量</td>
    </tr>
    <tr>
      <td>finder trade ...</td>
      <td>1</td>
      <td>1</td>
      <td>周围村民或流浪商人符合条件的交易选项的数量</td>
    </tr>
</table>

## 示例

- 查找周围30格以内的黑曜石
    - `/finder block minecraft:obsidian 30`

- 查找周围32格的容器内的烟花火箭
    - `/finder item minecraft:firework_rocket 32`

- 查找周围32格以内前15个出售绿宝石的村民
    - `/finder trade item minecraft:emerald 32 15`