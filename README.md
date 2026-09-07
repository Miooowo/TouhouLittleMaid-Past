# TouhouLittleMaid: Past

将 [Touhou Little Maid](https://github.com/TartaricAcid/TouhouLittleMaid) 移植到 **Minecraft Forge 1.7.10** 的独立仓库。功能对标上游 `1.20` 分支（Forge 1.20.1，版本 `1.5.3-forge+mc1.20.1`）。

> 这不是官方移植。1.20.1 → 1.7.10 几乎是重写：注册、实体、AI、渲染、网络、资源格式都不同。

## 当前进度（本阶段）

已完成可编译的 RetroFuturaGradle 工程，以及核心骨架：

- `modid`：`touhou_little_maid`（与 1.20.1 相同）
- 主类、创造栏、御币 / 智能硬盘 / 刷怪蛋召唤女仆
- `EntityMaid`：驯服、跟随、坐下待机、近战，以及耕作 / 甘蔗 / 瓜类 / 可可 / 剪毛 / 火把 / 挤奶 / 喂动物 / 喂主人 / 除草 / 铲雪 / 钓鱼 / 灭火
- 祭坛方块（单方块占位，不是 1.20.1 多方块仪式）
- 背包 / 座椅 / P 点 / 相机等核心物品 id 占位

完整功能对照与阶段划分见 [docs/PORTING.md](docs/PORTING.md)。

## 构建

最新 GTNH ExampleMod 的 Gradle 插件要求 **用 JDK 25 运行 Gradle**。游戏/模组字节码仍是 **Java 8**（`enableModernJavaSyntax = jabel`）。本机已安装：

- Temurin 8：`C:\Program Files\Eclipse Adoptium\jdk-8.0.504.1-hotspot`（目标运行时）
- Temurin 17：可用于日常，但跑不动当前 `gtnhgradle` 2.0.20
- Temurin 25：`C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot`（跑 `gradlew`）

```bat
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot
gradlew.bat build
```

产物在 `build/libs/`。

开发运行：

```bat
gradlew.bat runClient
```

## 游戏内最小操作

1. 合成或从创造栏取出 **博丽御币** / **女仆刷怪蛋** / **智能硬盘（初始）**
2. 对地面右键召唤已驯服女仆
3. 空手右键主人：打开女仆 GUI（背包 / 坐下 / 任务）
4. 潜行右键：坐下 / 站起；手持御币或 GUI「任务」：循环已实现任务（含耕作、甘蔗、瓜、可可、剪毛、火把、挤奶、喂食、除草、铲雪、钓鱼、灭火）
5. 空手盘右键女仆可收纳；已存储硬盘对地放出
6. 女仆死亡留下墓碑：右键取物，御币复活

## 许可

- 代码：MIT（[LICENSE-MIT](LICENSE-MIT)）
- 资源：CC BY-NC-SA 4.0（[LICENSE-CC](LICENSE-CC)）
- 说明：[LICENSE](LICENSE)、[NOTICE](NOTICE)

## 链接

- 本仓库：https://github.com/Miooowo/TouhouLittleMaid-Past
- 上游：https://github.com/TartaricAcid/TouhouLittleMaid
- 上游 fork：https://github.com/Miooowo/TouhouLittleMaid
