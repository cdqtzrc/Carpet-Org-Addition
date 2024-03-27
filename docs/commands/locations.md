路径点管理器，是MCDR中!!loc命令的替代命令，它不能指定编码保存，而是强制使用UTF-8编码以避免乱码问题

## 语法

`locations add <name> [<pos>]`

`locations delete <name>`

`locations list [<filter>]`

`locations <name> [<pos>]`

`locations set <name> [<pos>]`

`locations supplement <name> another_pos [<anotherPos>]`

`locations supplement <name> illustrate [<illustrate>]`

## 参数

`name`:string

路径点的名称

`pos`:block_pos

可选的方块坐标，如果未指定，默认为玩家当前位置

`filter`:string

如果不留空，只有名称包含此字符串的路径点才会被列出

`anotherPos`:block_pos

为路径点添加的另一个维度的坐标

`illustrate`:string

为路径点添加的说明文本，如果为空，或者字符串长度为0，表示删除已有的说明文本

## 效果

<table>
    <tbody>
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
            <td rowspan="3">/locations add ...</td>
            <td>执行成功</td>
            <td>添加一个路径点</td>
        </tr>
        <tr>
            <td>路径点已存在</td>
            <td>执行失败</td>
        </tr>
        <tr>
            <td>为自定义维度添加路径点</td>
            <td>执行失败</td>
        </tr>
        <tr>
            <td rowspan="2">locations delete ...</td>
            <td>执行成功</td>
            <td>删除一个路径点</td>
        </tr>
        <tr>
            <td>无法删除</td>
            <td>执行失败</td>
        </tr>
        <tr>
            <td>/locations list ...</td>
            <td>执行成功</td>
            <td>在聊天栏列出所有的路径点，无法解析的路径点被跳过</td>
        </tr>
        <tr>
            <td rowspan="2">/locations set ...</td>
            <td>执行成功</td>
            <td>将指定路径点修改为指定位置</td>
        </tr>
        <tr>
            <td>无法解析json文本</td>
            <td>执行失败</td>
        </tr>
        <tr>
            <td rowspan="3">/locations supplement &lt;suup&gt; another_pos ...</td>
            <td>执行成功</td>
            <td>为主世界路径点添加下界坐标，或者为下界路径点添加主世界坐标</td>
        </tr>
        <tr>
            <td>为末地添加对向坐标</td>
            <td>无法添加</td>
        </tr>
        <tr>
            <td>无法解析json文本</td>
            <td>执行失败</td>
        </tr>
        <tr>
            <td rowspan="3">locations supplement &lt;suup&gt; illustrate ...</td>
            <td>执行成功</td>
            <td>为指定路径点添加说明文本</td>
        </tr>
        <tr>
            <td>[&lt;illustrate&gt;]未指定，或字符串长度为0</td>
            <td>删除路径点已有的说明文本</td>
        </tr>
        <tr>
            <td>无法解析json文本</td>
            <td>执行失败</td>
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
            <td>除/locations list ...</td>
            <td>执行成功</td>
            <td>1</td>
            <td>1</td>
            <td>1</td>
        </tr>
        <tr>
            <td>/locations list ...</td>
            <td>执行成功</td>
            <td>1</td>
            <td>1</td>
            <td>列出路径点的个数</td>
        </tr>
    </tbody>
</table>

## 示例

- 列出所有路径点
    - `/locations list`

- 添加一个名为“路径点”指定坐标的路径点
    - `/locations add "路径点" 0 70 0`

- 删除一个名为“路径点”的路径点
    - `/locations delete "路径点"`