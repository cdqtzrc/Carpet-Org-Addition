在玩家之间分享经验，或者让真玩家从假玩家身上拿取经验，转移经验的数量有误差

## 语法

`xpTransfer <outputPlayer> <inputPlayer> (all|half)`

`xpTransfer <outputPlayer> <inputPlayer> level <level>`

`xpTransfer <outputPlayer> <inputPlayer> points <number>`

## 参数

`outputPlayer`:entity

向外输出经验的玩家

`inputPlayer`:entity

接受经验的玩家

`level`:integer

计算从0级升到<level>级需要的经验数量，然后转移该数量的经验

`number`:integer

转移指定数量的经验

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
      <td rowspan="4">任意</td>
      <td>参数未正确指定</td>
      <td>执行失败</td>
    </tr>
    <tr>
      <td>输出经验的玩家不是命令执行者自己或假玩家</td>
      <td>执行失败</td>
    </tr>
    <tr>
      <td>输出经验的玩家没有足够的经验</td>
      <td>执行失败</td>
    </tr>
    <tr>
      <td>执行成功</td>
      <td>转移指定数量的经验</td>
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
        <th rowspan="2">任意</th>
        <th>执行失败</th>
        <th>0</th>
        <th>0</th>
        <th>0</th>
    </tr>
    <tr>
        <th>执行成功</th>
        <th>1</th>
        <th>1</th>
        <th>转移经验的数量</th>
    </tr>
</table>

## 示例

- 将Steve所有的经验转移给Alex
    - `/xpTransfer Steve Alex all`
- 将自己一半的经验转移给Steve
    - `/xpTransfer @s Steve half`