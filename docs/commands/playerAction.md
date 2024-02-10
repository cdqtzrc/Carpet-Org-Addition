对假玩家执行一些操作，或者让假玩家自动做一些事情

## 语法

`playerAction <player> ...`

- `... clean`
- `... craft ...`
    - `... 2x2 <item1> <item2> <item3> <item4>`
    - `... 3x3 <item1> <item2> <item3> <item4> <item5> <item6> <item7> <item8> <item9>`
    - `... four <item>`
    - `... gui`
    - `... nine <item>`
    - `... one <item>`
- `... farming`
- `... fill <item>`
- `... info`
- `... rename <item> <name>`
- `... sorting <item> <this> <other>`
- `... stonecutting <item> <button>`
- `... stop`
- `... trade <index>`

## 参数

`player`:entity

必须是玩家名或指向玩家的目标选择器，且只能选中一个玩家

`item`,`item1~9`:item_predicate

一个指定的物品或存在物品形式的方块的ID

`name`:string
物品的新名称，中文字符或其他特殊字符需要用英文双引号包裹

`this`:vec3

一个坐标，假玩家会把指定的物品丢向这个坐标

`other`:vec3

一个坐标，假玩家会把指定物品以外的物品丢向这个坐标

`button`:integer

切石机按钮的索引，从1开始，必须大于0

`index`:integer

村民交易选项的索引，从1开始，必须大于0

## 效果

`/playerAction <player> ...`

- `clean`
    - 让假玩家自动清空潜影盒，需要让假玩家打开潜影盒，清空完毕后自动关闭潜影盒
- `craft ...`
    - 让假玩家自动合成一些物品，需要给予该假玩家一些合成材料物品
    - 为了快速合成物品，应同时启用Ctrl+Q合成修复
    - 如果在工作台合成，则需要打开一个工作台，并且不能使用副手槽和盔甲槽中的物品，在生存模式物品栏合成的，副手槽和盔甲槽的物品也会被使用
        - `2x2 <item1> <item2> <item3> <item4>`
            - 让假玩家在生存模式物品栏合成指定配方的物品
        - `3x3 <item1> <item2> <item3> <item4> <item5> <item6> <item7> <item8> <item9>`
            - 让假玩家在工作台合成指定配方的物品
        - `four`
            - 让假玩家在生存模式物品栏合成指定配方为四个相同材料的物品
        - `gui`
            - 打开一个GUI用来为假玩家指定配方，然后根据配方决定在生存模式物品栏还是工作台合成
        - `nine`
            - 让假玩家在工作台合成指定配方为九个相同材料的物品
        - `one`
            - 让假玩家在生存模式物品栏合成配方为单个材料的物品
- `farming`
    - 根据玩家副手的物品自动在周围种植并收获农作物，同时对农作物使用骨粉
- `fill <item>`
    - 让假玩家向潜影盒中放入指定物品，玩家物品栏内的指定物品会被填入潜影盒，非指定物品会被丢出，需要让假玩家打开潜影盒。
    - 无法再向潜影盒内填充物品时，潜影盒会自动关闭
- `info`
    - 在聊天栏显示假玩家当前动作的详细信息
- `rename <item> <name>`
    - 让假玩家给物品重命名，需要打开一个铁砧，重命名需要消耗经验
- `sorting <item> <this> <other>`
    - 让假玩家从一堆物品实体中分拣出指定的物品并丢在<this>的位置，其他物品物品会丢在<other>
      位置，潜影盒物品会先取出潜影盒内的物品，可以利用这个特性来快速拆解潜影盒
- `stonecutting <item> <button>`
    - 让假玩家使用切石机制作一些物品，需要让假玩家打开一个切石机GUI
- `stop`
    - 让假玩家停止当前的动作
- `trade <index>`
    - 让假玩家与一名村民或流浪商人进行交易，需要打开一个交易界面

## 输出

<table>
    <tr>
      <th>命令</th>
      <th>条件</th>
      <th>成功次数</th>
      <th>结果</th>
      <th>返回值</th>
    </tr>
  <tbody>
    <tr>
      <td rowspan="2">任意</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>1</td>
    </tr>
    <tr>
      <td>目标玩家不是假玩家</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
  </tbody>
</table>

## 示例

- 让Steve合成铁块
    - `/playerAction Steve craft nine minecraft:iron_ingot`
    - `/playerAction Steve craft 3x3 minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot minecraft:iron_ingot`
- 让Bot使用切石机制作石砖（如果修改了切石机的配方，按钮的索引也要相应调整）
    - `/playerActions Bot stonecutting minecraft:stone 5`
- 显示Alex正在做什么
    - `/playerAction Alex info`