允许打开假玩家的物品栏，末影箱，以及获取位置等

## 语法

`playerTools <player> (enderChest|heal|inventory|isFakePlayer|position|teleport)`

## 参数

`player`:entity

必须是玩家名或指向玩家的目标选择器，且只能选中一个玩家

## 效果

<table>
  <thead>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td rowspan="2">任意</td>
      <td>参数不正确</td>
      <td>无法解析</td>
    </tr>
    <tr>
      <td>&lt;player&gt;包含多个玩家或包含非玩家实体</td>
      <td rowspan="3">执行失败</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; ...，除isFakePlayer</td>
      <td>&lt;player&gt;不是假玩家</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; enderChest</td>
      <td>&lt;player&gt;不是自己或假玩家</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; enderChest</td>
      <td>执行成功时</td>
      <td>打开自己或假玩家的末影箱</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; inventory</td>
      <td>执行成功时</td>
      <td>打开假玩家的物品栏</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; isFakePlayer</td>
      <td>执行成功时</td>
      <td>在聊天栏输出指定玩家是否为假玩家</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; position</td>
      <td>执行成功时</td>
      <td>显示假玩家的位置</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; teleport</td>
      <td>执行成功时</td>
      <td>将假玩家传送到自己的位置，省去了先kill再spawn的麻烦</td>
    </tr>
  </tbody>
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
  <tbody>
    <tr>
      <td>任意</td>
      <td>执行失败</td>
      <td>0</td>
      <td>0</td>
      <td>0</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; enderChest</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>1</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; teleport</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>1</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; heal</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>假玩家回复的血量值向下取整的值</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; isFakePlayer</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>是真玩家返回1，是假玩家返回0</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; position</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>玩家距离目标假玩家的距离为命令的返回值，距离与维度无关</td>
    </tr>
    <tr>
      <td>/playerTools &lt;player&gt; inventory</td>
      <td>执行成功</td>
      <td>1</td>
      <td>1</td>
      <td>1</td>
    </tr>
  </tbody>
</table>

## 示例

- 回复Alex的生命值
    - `/playerTools Alex heal`
- 将Steve传送到自己的位置
    - `/playerTools Steve teleport`
- 判断Steve是否是假玩家
    - `/playerTools Steve isFakePlayer`