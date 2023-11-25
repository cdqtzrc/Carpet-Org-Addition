用来保护某一个假玩家不被/player命令杀死或不会死亡，需要启用规则“假玩家保护”

## 语法

`protect add <targets> [damage|death|kill]`

`protect list`

`protect remove all`

`protect remove name <player>`

## 参数

`target`:entity

要保护的玩家，可以是玩家名，也可以是目标选择器，但选择器的目标必须是玩家，且只能是一个玩家

`damage|death|kill`

受damage类型保护的假玩家，不会受到伤害，除非伤害类型带有"bypasses_invulnerability"标签或者伤害来自玩家的直接攻击

受death类型保护的假玩家，会受到所有伤害，但是当生命值归零时会立即回满，除非伤害类型带有"bypasses_invulnerability"
标签或者伤害来自玩家的直接攻击

受kill类型保护的假玩家，不会被/player <player> kill命令杀死，而且/player命令执行会失败

如果未指定，默认为kill

`player`:string

要删除的玩家名

## 效果

<table>
    <tbody>
        <tr>
           <th>命令</th>
           <th>触发条件</th>
           <th>结果</th>
        </tr>
        <tr>
           <td rowspan="2">任意</td>
           <td>参数不正确</td>
           <td>无法解析</td>
        </tr>
        <tr>
           <td>目标玩家不是假玩家</td>
           <td>执行失败</td>
        </tr>
        <tr>
           <td>/protect add &lt;player&gt; ...</td>
           <td>执行成功</td>
           <td>假玩家被保护，或修改假玩家的保护类型</td>
        </tr>
        <tr>
           <td>/protect list</td>
           <td>执行成功</td>
           <td>列出所有受保护的假玩家及保护类型</td>
        </tr>
        <tr>
           <td>/protect remove ...</td>
           <td>执行成功</td>
           <td>假玩家被取消保护</td>
        </tr>
    </tbody>
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
          <td>/protect add ...</td>
          <td>添加或修改成功</td>
          <td>1</td>
          <td>1</td>
          <td>1</td>
        </tr>
        <tr>
          <td>/protect add ...</td>
          <td>添加或修改失败</td>
          <td>1</td>
          <td>1</td>
          <td>0</td>
        </tr>
        <tr>
          <td>/protect list</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>列出玩家的数量</td>
        </tr>
        <tr>
          <td>/protect remove name ...</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>删除成功返回1，删除失败返回0</td>
        </tr>
        <tr>
          <td>/protect remove all</td>
          <td>执行成功</td>
          <td>1</td>
          <td>1</td>
          <td>删除玩家的数量</td>
        </tr>
    </tbody>
</table>

## 示例

- 保护Steve不受到伤害
    - `/protect add Steve damage`
- 列出所有受保护的假玩家
    - `/protect list`
- 让名为Alex的玩家取消受保护
    - `/protect remove name Alex`