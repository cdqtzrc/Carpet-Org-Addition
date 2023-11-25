用来在生存模式和旁观模式之间切换

## 语法

`spectator`

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
      <td rowspan="3">任意</td>
      <td>参数不正确</td>
      <td>无法解析</td>
    </tr>
    <tr>
      <td>命令执行者不是玩家</td>
      <td>执行失败</td>
    </tr>
    <tr>
      <td>执行成功</td>
      <td>如果玩家当前不是旁观模式，就切换到旁观模式，否则切换到生存模式</td>
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
        <td>任意</td>
        <td>执行成功</td>
        <td>1</td>
        <td>1</td>
        <td>玩家切换到生存模式返回1，否则返回0</td>
    </tr>
</table>