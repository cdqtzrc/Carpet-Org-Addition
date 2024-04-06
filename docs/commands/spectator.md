用来在生存模式和旁观模式之间切换

## 语法

`spectator [<player>]`

`spectator teleport dimension <dimension> [<location>]`

`spectator teleport entity <entity>`

## 参数

`player`:entity

必须是玩家名或指向玩家的目标选择器，且只能选中一个玩家

`dimension`:dimension

要传送到的维度

`location`:block_pos

传送到维度的指定坐标

`entity`:entity

传送到该实体的位置

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
      <td>任意</td>
      <td>参数不正确</td>
      <td>无法解析</td>
    </tr>
    <tr>
     <td rowspan="3">spectator [&lt;player&gt;]</td>
      <td>未加[&lt;player&gt;]参数时命令执行者不是玩家</td>
      <td rowspan="2">执行失败</td>
    </tr>
    <tr>
      <td>加了[&lt;player&gt;]参数时目标玩家不是假玩家</td>
    </tr>
    <tr>
      <td>执行成功</td>
      <td>如果玩家当前不是旁观模式，就切换到旁观模式，否则切换到生存模式</td>
    </tr>
    <tr>
        <td>spectator teleport ...</td>
        <td>玩家当前未处于旁观模式</td>
        <td>执行失败</td>
    </tr>
    <tr>
      <td>spectator teleport dimension &lt;dimension&gt; [&lt;location&gt;]</td>
      <td>执行成功</td>
      <td>玩家传送到指定维度，如果指定了&lt;location&gt;，则传送指定维度的指定坐标</td>
    </tr>
    <tr>
      <td>spectator teleport entity &lt;entity&gt;</td>
      <td>执行成功</td>
      <td>玩家传送到指定实体的位置</td>
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
        <td>spectator</td>
        <td rowspan="2">执行成功</td>
        <td>1</td>
        <td>1</td>
        <td>玩家切换到生存模式返回1，否则返回0</td>
    </tr>
    <tr>
        <td>任意，除了spectator</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
    </tr>
</table>