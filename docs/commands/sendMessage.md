用来发送一些可以轻易被复制的文本，或者发送一条网页链接，或发送玩家自己的位置

## 语法

`sendMessage copy <text>`

`sendMessage location`

`sendMessage url <url>`

`sendMessage color <color> <text>`

`sendMessage strikethrough <text>`

`sendMessage formatting <text>`

## 参数

`text`

要发送文本的内容，包含中文或其他特殊字符需要英文双引号

`url`

要发送的网页链接，包含中文或其他特殊字符需要英文双引号

## 效果

<table>
    <tbody>
        <tr>
         <th>命令</th>
         <th>触发条件</th>
         <th>结果</th>
         <th>备注</th>
        </tr>
        <tr>
         <td>任意</td>
         <td>参数不正确</td>
         <td>无法解析</td>
         <td></td>
        </tr>
        <tr>
         <td>/sendMessage copy ...</td>
         <td>执行成功</td>
         <td>在聊天栏发送一条可以单击复制的文本</td>
         <td rowspan="7">如果命令执行者是玩家，还会在文本前追加玩家名</td>
        </tr>
        <tr>
         <td>/sendMessage location</td>
         <td>命令执行者不是玩家</td>
         <td>执行失败</td>
        </tr>
        <tr>
         <td>/sendMessage location</td>
         <td rowspan="5">执行成功</td>
         <td>在聊天栏发送一条包含自己所在维度和坐标，以及对应的主世界或下界坐标的文本</td>
        </tr>
        <tr>
         <td>/sendMessage url ...</td>
         <td>在聊天栏发送一条可以单击打开的网页链接，如果命令执行者是玩家，还会在链接文本前追加玩家名</td>
        </tr>
        <tr>
         <td>/sendMessage color ...</td>
         <td>在聊天栏发送一条带颜色的消息</td>
        </tr>
        <tr>
         <td>/sendMessage strikethrough ...</td>
         <td>在聊天栏发送一条带删除线的消息</td>
        </tr>
        <tr>
         <td>/sendMessage formatting ...</td>
         <td>在聊天栏发送一条可以被格式化的消息，使用"$"代替"§"</td>
        </tr>
    </tbody>
</table>

其中:

- `/sendMessage location`是MCDR中!!here的替代命令

## 输出

<table>
    <tbody>
      <tr>
        <th>命令</th>
        <th>条件</th>
        <th>成功次数</th>
        <th>结果</th>
        <th>返回值</th>
     </tr>
      <tr>
        <td rowspan="2">任意</td>
        <td>执行成功时</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
     </tr>
      <tr>
        <td>执行失败时</td>
        <td>0</td>
        <td>0</td>
        <td>0</td>
     </tr>
    </tbody>
</table>

## 示例

- 在聊天栏发送自己的位置
    - `/sendMessage location`

- 使用绿色的字发“你好”
    - `/sendMessage color green "你好"`