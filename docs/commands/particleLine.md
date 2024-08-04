使用粒子绘制一条线，可以用来指示方向

## 语法

`particleLine <from> <to>`

`particleLine <from> <uuid>`

## 参数

`from`

线的起始端点

`to`

线的结束端点

`uuid`

目标实体的UUID

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
      <td rowspan="4">任意</td>
      <td>参数不正确</td>
      <td>无法解析</td>
    </tr>
    <tr>
      <td>命令执行者不是玩家</td>
      <td rowspan="2">执行失败</td>
    </tr>
    <tr>
      <td>无法解析UUID或指定UUID的实体不存在</td>
    </tr>
    <tr>
      <td>执行成功</td>
      <td>绘制一条粒子组成的线，从起点向终点绘制</td>
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
        <td rowspan="2">任意</td>
        <td>执行失败</td>
        <td>0</td>
        <td>0</td>
        <td>0</td>
    </tr>
    <tr>
        <td>执行成功</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
    </tr>
</table>