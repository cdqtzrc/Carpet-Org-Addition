# Carpet-Org-Addition

## 规则

制作物品分身(makeItemShadowing)

- 启用命令/itemshadowing以制作物品分身
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`指令`

设置基岩硬度(setBedrockHardness)

- 将基岩硬度设置为指定值
- 值必须大于等于0，或者为-1
- 需要客户端支持
    - 类型：`单精度浮点数`
    - 默认值：`-1`
    - 分类：`Org`

绑定诅咒无效化(bindingCurseInvalidation)

- 启用后玩家可以取下带有绑定诅咒附魔的装备
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

禁用钓鱼开放水域检测(disableOpenOrWaterDetection)

- 启用后玩家可以在封闭水域中钓鱼并获得宝藏
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

幽匿尖啸体可生成监守者(sculkShriekerCanSummon)

- 启用后玩家手动放置的幽匿尖啸体默认可以生成监守者
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

村民立即补货(villagerImmediatelyRestock)

- 启用后每次与村民交互前村民会进行一次补货
- 每次与村民交易完毕后可以重新打开交易GUI来进行补货
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家保护(fakePlayerProtect)

- 被保护的假玩家不会被/player `<玩家名称>` kill杀死
    - 保护类型
        - none：默认，假玩家不受baoh
        - kill：假玩家不能被/player命令杀死
        - damage：假玩家只会受到直接来自玩家的伤害和具有"bypasses_invulnerability"标签的伤害
        - death：假玩家只会被直接来自玩家的伤害和具有"bypasses_invulnerability"标签的伤害杀死
            - 类型：`布尔值`
            - 默认值：`false`
            - 参考选项：`true`，`false`
            - 分类：`Org`，`特性`

受保护玩家列表控制(commandProtect)

- 启用/protect命令用来管理受保护玩家列表
- 不能重复添加
- 必须是假玩家
- 假玩家必须存在于世界中
- 尝试使用/player `<玩家名称>`
  kill击杀受保护的假玩家会失败并显示一条错误提示，但直接使用/kill命令可以杀死
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`指令`

创造玩家免疫/kill(creativeImmuneKill)

- 启用后命令/kill不能杀死创造模式的玩家
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`创造`

掉落物不消失(itemNeverDespawn)

- 启用后掉落物会一直保存在世界中
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

荆棘不额外消耗耐久(thornsDamageDurability)

- 造成荆棘伤害后不会额外消耗耐久度
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`，`特性`

滑翔时不能对方块使用烟花(flyingUseOnBlockFirework)

- 使用鞘翅飞行时，烟花火箭只能用来飞行
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

注视末影人眼睛时不会激怒末影人(staringEndermanNotAngry)

- 末影人不会因为玩家盯着它的眼睛而被激怒，即便玩家没有佩戴雕刻南瓜
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`，`特性`

耕地防踩踏(farmlandPreventStepping)

- 耕地不会因为生物踩踏而退化成泥土
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`，`特性`

凋零骷髅可生成在传送门方块(witherSkeletonCanSpawnToPortal)

- 凋零骷髅可以生成下界传送门方块或其他亮度不高于11的方块上
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

最大方块交互距离(maxBlockInteractionDistance)

- 服务器不会拒绝在此距离内的玩家操作
- 默认情况下不会影响与实体的交互，需要开启"最大方块交互距离适用于实体"
- 允许玩家使用Tweakeroo等模组修改交互距离，也可以让投影打印机支持更大的范围
- 值必须介于0-128之间，或者是-1
- 需要客户端支持
    - 类型：`双精度浮点数`
    - 默认值：`-1`
    - 分类：`Org`，`生存`，`特性`

简易更新跳略器(simpleUpdateSkipper)

- 通过让红石线不连接四周开启的活板门上的红石线来恢复1.20-pre2前的更新跳略器
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

强化引雷(ChannelingIgnoreWeather)

- 三叉戟引雷时忽略天气
- 依然会受其他条件限制，如维度，是否露天等
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

末影珍珠生成末影螨概率(enderPearlSpawnEndermiteProbability)

- 设置末影珍珠生成末影螨的概率值
- 值必须介于0-1直接，或者-1
    - 类型：`单精度浮点数`
    - 默认值：`-1`
    - 分类：`Org`，`特性`

破坏冰时总是变成水(iceBreakPlaceWater)

- 破坏冰时不需要下方为可阻止移动方块或液体就可以变成水
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

无伤末影珍珠(notDamageEnderPearl)

- 使用末影珍珠不会受到摔落伤害
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

保护类魔咒兼容(protectionEnchantmentCompatible)

- 所有的保护类附魔可以共存
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁用伤害免疫(disableDamageImmunity)

- 阻止游戏限制生物受伤频率
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止传送门更新(disablePortalUpdate)

- 下界传送门方块收到方块更新后不会做出反应
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`创造`

干草捆完全抵消摔落伤害(hayBlockCompleteOffsetFall)

- 落在干草捆上时不会受到摔落伤害
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

蓝冰上不能刷怪(blueIceCanSpawn)

- 生物不能在蓝冰上生成
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止蝙蝠生成(disableBatCanSpawn)

- 阻止蝙蝠自然生成
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

海龟蛋快速孵化(turtleEggFastHatch)

- 海龟蛋即使在白天也可以快速孵化
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

大范围信标(wideRangeBeacon)

- 信标作用效果向各个方向扩展50格，再向下额外延伸384格
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

禁止海带生长(disableKelpGrow)

- 阻止海带自然生长
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

强制开启潜影盒(openShulkerBoxForcibly)

- 允许玩家打开被阻挡的潜影盒
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

村民无限交易(villagerInfiniteTrade)

- 与村民交易时不会增加交易次数
- 与村民立即补货的区别是，这不需要重新打开村民交易GUI
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

烟花火箭使用冷却(fireworkRocketUseCooldown)

- 使用烟花火箭后有5个游戏刻的冷却时间
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

强化激流(riptideIgnoreWeather)

- 使用激流时忽略天气
- 忽略天气，也忽略使用激流的其他条件
- 需要客户端支持
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止猪灵僵尸化(disablePiglinZombify)

- 猪灵在下界以外的维度时不会变成僵尸猪灵
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

猪灵快速交易(piglinFastBarter)

- 猪灵完成以物易物只需要8个游戏刻
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止村民女巫化(disableVillagerWitch)

- 村民被闪电击中后不会变成女巫
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止铁傀儡攻击玩家(disableIronGolemAttackPlayer)

- 铁傀儡永远不会试图攻击玩家，即使这只铁傀儡不是玩家建造的
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

将镐作为基岩的有效采集工具(pickaxeMinedBedrock)

- 使用镐可以更快速的破坏基岩
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止雪傀儡融化(disableSnowGolemMelts)

- 雪傀儡可以在任何群系生存
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

村民回血(villagerHeal)

- 村民每4秒回复1生命值
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家回血(fakePlayerHeal)

- 假玩家每2秒回复1生命值
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

伤害附魔兼容(damageEnchantmentCompatible)

- 所有的伤害类附魔可以共存
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

假玩家工具命令(commandPlayerTools)

- 提供一些假玩家相关的辅助工具
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

高精度弓(highPrecisionBow)

- 弓射出的箭不会产生随机偏移
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止岩浆怪生成在下界荒地(disableMagmaCubeSpawnNetherWastes)

- 岩浆怪不会在下界荒地生成
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

最大方块交互距离适用于实体(maxBlockInteractionDistanceReferToEntity)

- 玩家可以与最大方块交互距离内的实体交互
- 最大方块交互距离默认不会影响与实体交互，需要开启本条规则
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

可再生迅捷潜行(renewableSwiftSneak)

- 图书管理员可以交易迅捷潜行附魔书
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

传送门生成僵尸猪灵概率(portalSpawnZombifiedPiglinProbability)

- 下界传送门方块接收到随机刻后有指定值/2000的概率生成僵尸猪灵
- 值必须介于0-2000之间，或者-1
    - 类型：`整数`
    - 默认值：`-1`
    - 分类：`Org`，`特性`

击退棒(knockbackStick)

- 启用后可以给木棍附魔击退
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

禁止重生方块爆炸(disableRespawnBlocksExplode)

- 玩家在非主世界维度使用床或在非下界维度使用重生锚时不会爆炸
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

CCE更新抑制器(CCEUpdateSuppression)

- 通过将潜影盒命名为"更新抑制器"来制作基于"ClassCastException"的更新抑制器
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

开放/seed命令权限(openSeedPermissions)

- 允许无权限玩家在服务器使用/seed命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`

发送可复制文本命令(commandSendMessage)

- 可以让玩家发送一些可以复制的文本、链接，以及自己的位置
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

开放/carpet命令权限(openCarpetPermissions)

- 仅在单人游戏有效
- 允许玩家在无作弊的单人游戏中使用/carpet命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`，`客户端`

开放/gamerule命令权限(openGameRulePermissions)

- 允许无权限玩家使用/gamerule命令
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`命令`

打开村民物品栏(openVillagerInventory)

- 在潜行时对村民右键可以打开村民物品栏
- 因为村民只有8格物品栏，所以在打开GUI中，第九个格子不会存储物品，玩家在关闭GUI时物品会自动丢出
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`特性`

和平的苦力怕(peacefulCreeper)

- 苦力怕不会与玩家敌对
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

经验转移(commandXpTransfer)

- 启用/xpTransfer命令用来在玩家间互相分享经验
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

生存旁观切换命令(commandSpectator)

- 允许玩家在生存模式和旁观模式间切换
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

方块查找器(commandBlockFinder)

- 启用/blockFinder命令用来在指定范围内查找特定方块
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

自杀命令(commandKillMe)

- 启用/killMe命令用来自杀
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

路径点管理器(commandLocations)

- 启用/locations命令用来管理路径点
    - 类型：`字符串`
    - 默认值：`"ops"`
    - 参考选项：`"true"`, `“false”`, `"ops"`, `"0"`, `"1"`, `"2"`,
      `"3"`, `"4"`
    - 分类：`Org`，`命令`

生命恢复附带饱和(regenerationSaturation)

- 生命恢复会同时给予饱和效果
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

血量不满时可进食(healthNotFullCanEat)

- 血量没有完全恢复并且饱和度小于等于5时可以进食
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

可采集刷怪笼(canMineSpawner)

- 刷怪笼可以使用精准采集工具采集
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
    - 分类：`Org`，`生存`

假玩家生成时不保留击退(fakePlayerSpawnNotRetainKnockback)

- 假玩家生成时不会保留上一次的击退、着火时间，摔落距离
    - 类型：`布尔值`
    - 默认值：`false`
    - 参考选项：`true`，`false`
- 分类：`Org`，`特性`

可解析路径点(canParseWayPoint)

- 通过移除路径点中的交互事件来让OMMC可以解析聊天中的路径点
    - 类型：布尔值
    - 默认值：false
    - 参考选项：true，false
    - 分类：Org，生存

禁用无序聊天数据包检查(disableOutOfOrderChatCheck)

- 玩家发送无序的聊天数据包时不会被踢出游戏
    - 类型：布尔值
    - 默认值：false
    - 参考选项：true，false
    - 分类：Org，生存

## 命令

### /itemshadowing

- 用来制作物品分身，没有子命令
- 使用后会将主手的itemStack对象赋值到副手
- 使用时主手不能为空，但副手必须为空

### /protect

- 用来管理受保护的玩家列表，有3条子命令
    - add
        - 添加玩家，必须是假玩家，正常情况下真玩家不能被添加到列表
        - 添加的玩家必须是游戏内实际存在的
        - 不能重复添加
            - 假玩家的保护类型
    - list
        - 列出受保护玩家列表玩家总数、所有玩家的名字，以及假玩家当前的保护类型
    - remove
        - 从列表中删除玩家，有3条子命令
            - name
                - 根据玩家名称删除玩家
            - all
                - 删除列表内所有玩家
                - 删除完成后在聊天栏输出删除玩家的数量

### /playerTools

- 提供一些关于假玩家的辅助工具
    - `<目标选择器>`：只允许选择一个玩家
        - enderChest
            - 用来远程打开假人末影箱，也可以打开自己的
        - teleport
            - 用来传送假玩家，且只能是假玩家
            - 可以将假玩家传送到自己的位置
            - 可以跨维度传送
            - 传送后假玩家和自己有相同的朝向
        - isFakePlayer
            - 判断指定玩家是否为假玩家
        - position
            - 在聊天栏输出假玩家所在的维度和位置
        - heal
            - 回满假玩家血量
                - 同时回满假玩家饥饿值
        - action
            - 让假玩家执行一些操作
            - sorting
                - 让假玩家分拣特定的物品
                    - 要分拣的物品的id
                    - 丢出潜影盒时，会连同潜影盒内的物品一起丢出
                        - 要把需要分拣的物品丢到哪里，参数是一个三维坐标，假人会看向这里，然后丢出手中物品
                            - 要把非分拣的物品丢到哪里，同上
            - clean
                - 让假玩家清空潜影盒物品
                - 需要让假玩家打开一个潜影盒界面
            - fill
                - 让假玩家使用特定的物品填充潜影盒
                - 需要让假玩家打开一个潜影盒界面
                    - 要填充的物品
            - stop
                - 让假玩家停止操作
            - craft
                - 让假玩家合成一些单一合成材料的物品，有两条子命令
                - 需要让假玩家打开一个工作台界面
                    - one子命令，用来合成配方只有一个材料的物品
                        - 要合成的物品
                    - nine子命令，用来合成配方是九个相同材料的物品
                        - 要合成的物品
                    - four子命令，用来合成配方是四个相同材料的物品
                        - 要合成的物品
            - query
                - 查询假玩家当前的操作类型
            - action
                - 用来让假人自动给物品重命名
                - 重命名需要消耗经验
                    - 要重命名的物品
                        - 物品的新名字
            - stonecutting
                - 用来让假人自动使用切石机
                    - 要切制的物品id
                        - 要点击的按钮，为整数形式，并且不能小于1，表示要点击按钮的索引

### /sendMessage

- 用来发送一些消息，有3条子命令
    - copy
        - 发送一条可以单击复制的文本
            - 要发送的消息文本，不需要双引号
                - 如果命令执行者是玩家，会在发送的消息前显示玩家名
    - url
        - 发送一条网页链接
            - 要发送的网页链接
                - 如果命令执行者是玩家，会在发送的消息前显示玩家名
    - location
        - 发送玩家自己所在的位置，后面没有其他参数
            - 如果玩家位于主世界，那么会同时显示主世界和对应下界的坐标，玩家位于下界时同理
            - 坐标可以单击复制
            - 坐标的颜色根据所在的维度变化
            - 命令执行者只能是玩家

### /xpTransfer

- `<玩家名>`
- 向外转移经验的玩家
- 需要是假玩家或者执行本条命令的玩家
    - `<玩家名>`
    - 接受经验的玩家
        - all
            - 转移所有的经验
        - half
            - 转移一半的经验
        - points
            - 转移指定数量的经验
                - 转移经验的数量
                    - 不能大于输出经验的玩家的总经验
        - level
            - 转移从0级升到指定等级需要的经验
                - 指定等级

### /spectator

- 在生存模式和旁观模式间切换，没有子命令

### /blockFinder

- 用来查找玩家周围的指定方块
- 要查找方块的ID
    - 要查找的范围
        - 范围是一个以玩家为中心，向四周扩展指定格数，高度为整个世界高度的空间
        - 值需要介于0-128之间
        - 如果3秒没未完成方块查找，查找会失败

### /killMe

- 自杀，没有子命令

### /locations

- 路径点管理器
    - add
        - 添加路径点
            - 路径点的名称
            - 如果后面没有其他参数，添加玩家当前位置为路径点
                - 路径点的位置坐标
    - delete
        - 删除路径点
            - 要删除路径点的名称
    - info
        - 显示路径点的详细信息，包括路径点的创建者、创建时间、路径点的说明文本，与该路径点的距离
            - 路径点的名称
    - list
        - 列出所有路径点
            - 如果后面还有字符串参数，则只列出包含该字符串的路径点
    - supplement
        - 为路径点添加其他信息
            - 路径点名称
                - another_pos
                    - 添加对向坐标，如果后面没有其他参数，添加玩家当前位置
                    - 末地坐标不能添加
                        - 路径点的位置坐标
                - illustrate
                    - 添加说明文本
