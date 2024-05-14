用来指引玩家前往某一个位置

## 语法

`navigate blockPos <blockPos>`：指引玩家前往指定坐标

`navigate entity <entity> [continue]`

`navigate lastDeathLocation [<player>]`

`navigate uuid <uuid>`

`navigate waypoint <waypoint>`

`navigate (stop|spawnpoint)`

## 参数

`blockPos`:blockPos

目的地的坐标

`entity`:entity

目标实体

`player`:entity

目标玩家

`uuid`:string

目标实体的UUID

`waypoint`

目标路径点

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
            <td>/navigate 除了stop</td>
            <td rowspan="3">执行成功</td>
            <td>指引玩家到目的地</td>
        </tr>
        <tr>
            <td>/navigate entity &lt;entity&gt; continue</td>
            <td>持续指引玩家到指定实体位置，直到目标实体消失或主动停止导航</td>
        </tr>
        <tr>
            <td>/navigate stop</td>
            <td>停止导航</td>
        </tr>
    </tbody>
</table>

## 效果

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
    </tbody>
</table>