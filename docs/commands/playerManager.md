用来快速上线和下线常用假玩家

## 语法

- `playerManager (save|resave) <player> [<annotation>]`
- `playerManager (spawn|delete) <name>`
- `playerManager list`
- `playerManager schedule`
    - `... relogin <name> <interval>`：用来控制假玩家每隔指定时间重复的上线下线
    - `... relogin <name> stop`：停止上述操作
    - `... (login|logout) <name> <delayed> (h|min|s|t)`：用来控制指定假玩家在指定时间后上线/下线
    - `... cancel <name>`：取消指定玩家的上线/下线计划
    - `... list`：列出所有上述计划

## 参数

`player`：entity

必须是玩家名或指向玩家的目标选择器，且只能选中一个玩家

`name`：string

已保存玩家的名称

`annotation`：string

保存假玩家时添加的注释

`interval`:integer

假玩家重复上下线的时间间隔

`delayed`:integer

假玩家等待上线下线的时间

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
      <td>playerManager save &lt;player&gt;</td>
      <td rowspan="10">执行成功</td>
      <td>将一名假玩家信息保存到本地文件</td>
    </tr>
    <tr>
      <td>playerManager save &lt;player&gt;</td>
      <td>将一名假玩家信息保存或重新到本地文件</td>
    </tr>
    <tr>
      <td>playerManager delete &lt;name&gt;</td>
      <td>删除一个假玩家的信息</td>
    </tr>
    <tr>
      <td>playerManager spawn &lt;name&gt;</td>
      <td>根据本地文件的内容生成一个假玩家</td>
    </tr>
    <tr>
      <td>playerManager list</td>
      <td>列出所有已保存的假玩家</td>
    </tr>
    <tr>
      <td>... schedule relogin ...</td>
      <td>让假玩家周期性的上线下线</td>
    </tr>
    <tr>
      <td>... schedule login ...</td>
      <td>让假玩家在指定时间后上线</td>
    </tr>
    <tr>
      <td>... schedule logout ...</td>
      <td>让假玩家在指定时间后下线</td>
    </tr>
    <tr>
      <td>... schedule cancel ...</td>
      <td>取消上述上线下线的计划</td>
    </tr>
    <tr>
      <td>... schedule list</td>
      <td>列出上述所有计划</td>
    </tr>
</table>

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
          <td>任意</td>
          <td>执行失败</td>
          <td>0</td>
          <td>0</td>
          <td>0</td>
        </tr>
        <tr>
          <td>任意，除了list和schedule子命令</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>1</td>
        </tr>
        <tr>
          <td>playerManager list子命令</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>列出玩家的数量</td>
        </tr>
        <tr>
          <td>... schedule relogin ...</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>上下线的时间间隔</td>
        </tr>
        <tr>
          <td>... schedule login|logout ...</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>上线/下线的等待时间</td>
        </tr>
        <tr>
          <td>... schedule cancel ...</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>取消计划的数量</td>
        </tr>
        <tr>
          <td>... schedule list</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>列出计划的数量</td>
        </tr>
    </tbody>
</table>

## 示例

- 将Steve的玩家信息保存到本地文件
    - `/playerManager save Steve`
- 生成Steve
    - `/playerManager spawn Steve`
- 列出所有已保存的假玩家
    - `/playerManager list`