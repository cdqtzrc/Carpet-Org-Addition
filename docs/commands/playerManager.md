用来快速上线和下线常用假玩家

## 语法

- `playerManager (save|resave) <player> [<annotation>]`
- `playerManager (spawn|delete) <name>`
- `playerManager list`

## 参数

`player`：entity

必须是玩家名或指向玩家的目标选择器，且只能选中一个玩家

`name`：string

已保存玩家的名称

`annotation`：string

保存假玩家时添加的注释

## 效果

<table>
    <tr>
      <th>命令</th>
      <th>触发条件</th>
      <th>结果</th>
    </tr>
    <tr>
      <td>playerManager save &lt;player&gt;</td>
      <td rowspan="5">执行成功</td>
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
          <td>任意，除了list子命令</td>
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
    </tbody>
</table>

## 示例

- 将Steve的玩家信息保存到本地文件
    - `/playerManager save Steve`
- 生成Steve
    - `/playerManager spawn Steve`
- 列出所有已保存的假玩家
    - `/playerManager list`