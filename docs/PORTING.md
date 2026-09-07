# TouhouLittleMaid: Past 移植路线

对标上游：<https://github.com/TartaricAcid/TouhouLittleMaid>  
默认分支 `1.20`（Forge **1.20.1**，`mod_version=1.5.3-forge+mc1.20.1`，Forge `1.20.1-47.2.0`）。

本地参考源码（不要提交进本仓库）：`C:\Users\OEM\Projects\_ref\TouhouLittleMaid`  
上游 fork：https://github.com/Miooowo/TouhouLittleMaid  
本移植仓库：https://github.com/Miooowo/TouhouLittleMaid-Past

## 上游快照（调研结论）

| 项 | 1.20.1 现状 |
| --- | --- |
| modid | `touhou_little_maid` |
| 包名 | `com.github.tartaricacid.touhoulittlemaid` |
| 主类 | `TouhouLittleMaid` |
| 代码规模 | 约 1489 个 Java 文件；实体相关 300+；任务/Brain 100+ |
| 资源 | 运行时模型包 / GeckoLib / 基岩实体模型；仓库内几乎无散落 png（默认包另存） |
| 许可证 | 代码 MIT；资源 CC BY-NC-SA 4.0 |
| 硬依赖倾向 | Forge 1.20.1、GeckoLib（运行时）、Mixin |
| 软依赖 | JEI / REI / EMI、Patchouli、Jade、TOP、KubeJS、Curios、Farmers Delight、枪支/拔刀剑等大量兼容 |

主要模块：女仆实体与 Brain 任务树、背包与饰品、祭坛多方块、家具（棋类 / 电脑 / 键盘 / 书架）、工坊与雕像、音游、模型包加载、网络包、数据驱动任务/战利品、AI 聊天。

1.20.1 已注册任务 id：`idle`、`attack`、`ranged_attack`、`crossbow_attack`、`danmaku_attack`、`trident_attack`、`farm`、`sugar_cane`、`melon`、`cocoa`、`honey`、`grass`、`snow`、`feed`、`shears`、`milk`、`torch`、`feed_animal`、`fishing`、`extinguishing`、`board_games`。

## 移植策略（已选定）

1. **本仓库只放 1.7.10 重写代码**，不整仓复制 1.20.1 源码。
2. **工程**：GTNH ExampleMod1.7.10 + RetroFuturaGradle，Forge `10.13.4.1614`，Java 8 字节码。
3. **功能对标 1.20.1**，实现手法参考 1.12.2 分支的 EntityAI（更接近 1.7.10），而不是照搬 Brain/Sensor。
4. **渲染**：本阶段用 `ModelBiped` + 占位贴图。GeckoLib / 基岩模型包放到后续阶段，再评估 1.7.10 动画库或自研。
5. **id 尽量对齐**：modid、物品/方块/任务 registry name 与 1.20.1 相同，方便以后对照。

## 本阶段已移植

- Forge 1.7.10 工程（`gradlew`、`mcmod.info`、无 `mods.toml`）
- 主类 / 代理 / 配置 / 创造栏
- `EntityMaid`（`EntityTameable`，碰撞箱 0.6×1.5）
- 生成：博丽御币、东风御币、智能硬盘（初始）、女仆刷怪蛋
- AI：坐下待机、跟随主人、近战保护（`attack` 任务）
- 任务表：全部 1.20.1 id 已登记，仅 `idle` / `attack` 生效
- 15 格背包 NBT（无 GUI）
- 祭坛单方块 + 核心物品占位

## 可移植但需降级（后续阶段）

| 功能 | 1.7.10 做法 |
| --- | --- |
| 任务树（耕作、剪毛、挤奶、火把、喂食、钓鱼等） | `EntityAI` 逐个重写，无 Brain |
| 女仆 GUI / 背包 / 饰品 | `Container` + `GuiContainer`；无 Capability |
| 智能硬盘存储女仆 | 物品 NBT 写入整只实体，替代 1.20 data component |
| 座椅 / 扫帚 / 墓碑 / P 点实体 | `Entity` 手写同步 |
| 弹幕 | 自定义投射物，无 GeckoLib 弹幕模型 |
| 祭坛多方块 | 结构检测手写，或继续单方块简化 |
| 网络 | `SimpleNetworkWrapper` |
| 模型包 | 自研加载器或简化 json；不能直接用 1.20 资源包格式 |
| 语音包 | 1.7.10 sounds.json 不存在，用 `sounds.xml` / `SoundEvent` 注册 |
| Patchouli 手册 | 降级为原版书或自定义 GUI |
| JEI | 对接 NEI（1.7.10 生态） |

## 本阶段明确不做

- GeckoLib 动画、YSM 兼容、默认高质量模型包
- 音游、五子棋 / 象棋 AI、野餐垫、神龛完整逻辑
- KubeJS / Curios / 现代枪械 / 农夫乐事等兼容
- Mixin、数据生成、进度/触发器系统
- LLM / AI 聊天
- 1.20 数据包、tags、loot modifier
- 把整个 `_ref` 上游源码提交进本仓库

## 阶段划分

1. **骨架（本轮）**：工程编译 + 女仆能生成、渲染、跟随/待机/近战。
2. **生存循环**：GUI、背包、硬盘收纳、墓碑、基础合成与掉落。
3. **任务**：耕作、收集、喂食、照明、剪毛、挤奶。
4. **战斗扩展**：弓、弹幕、灭火、武器损坏。
5. **世界内容**：祭坛仪式、灯笼、稻草人、床、家具。
6. **表现**：模型包、语音、动画替代方案。
7. **兼容**：NEI、Baubles（若需要）、常见 1.7.10 模组。

## 已知缺口

- 本机已补装 Temurin 8（游戏目标）和 Temurin 25（跑最新 GTNH Gradle 插件）。默认 `java` 仍是 17，构建时需把 `JAVA_HOME` 指到 JDK 25。RFG/Jabel 产出 Java 8 字节码。
- 已验证：`JAVA_HOME=...\jdk-25.0.4.101-hotspot` 后 `gradlew.bat build` → **BUILD SUCCESSFUL**（约 3m 33s）。
- 占位贴图不是上游美术；正式资源需按 CC BY-NC-SA 从上游模型包适配。
- 御币直接点地召唤，是为了先玩起来；1.20.1 是祭坛仪式。
- 坐下用原版 `EntityAISit`，没有 1.20 的日程表（工作/娱乐/睡觉）。
- 尚未 `git commit` / `git push`（按用户规则：未要求提交则不提交）。远程空仓库已创建，本地 `origin` 已指向它。
