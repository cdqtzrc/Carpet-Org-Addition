搜索翻译后名称中包含指定字符串的规则

## 语法

`ruleSearch <rule>`

## 效果

<table>
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
      <td>执行成功</td>
      <td>列出翻译后名称中包含指定字符串的规则</td>
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
        <td>列出规则的数量</td>
    </tr>
</table>