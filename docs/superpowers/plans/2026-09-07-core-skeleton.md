# Core Skeleton Implementation Plan

> **For agentic workers:** 本计划已在同一会话内联执行。后续阶段请按 `docs/PORTING.md` 开新计划。

**Goal:** 可编译的 Forge 1.7.10 工程，女仆能生成、渲染、跟随/待机/近战。

**Architecture:** RetroFuturaGradle 工程 + 1.7.10 手写注册。功能对标 1.20.1，API 用 1.7.10。

**Tech Stack:** Forge 1.7.10 (10.13.4.1614), Java 8 bytecode, GTNH ExampleMod starter, JDK 17/21 运行 Gradle。

**Spec:** `docs/superpowers/specs/2026-09-07-tlm-past-design.md`

## Global Constraints

- 目标 Minecraft 1.7.10 / Forge 10.13.4.1614
- 不把上游 1.20.1 源码当本仓正文
- 不擅自 commit/push（用户未要求提交）
- 仓库名 TouhouLittleMaid-Past，description 含 Past 与 1.7.10 port

---

### Task 1: 工程与远程

- [x] 使用 ExampleMod1.7.10 starter
- [x] 替换 gradle.properties 占位符
- [x] `gh repo fork TartaricAcid/TouhouLittleMaid`
- [x] `gh repo create TouhouLittleMaid-Past`

### Task 2: 核心代码

- [x] 主类、代理、配置、创造栏
- [x] EntityMaid + 任务枚举 + 跟随/待机/近战
- [x] 御币、硬盘、刷怪蛋、祭坛、占位物品
- [x] RenderMaid + 语言文件 + 占位贴图

### Task 3: 验证

- [x] `gradlew.bat build` → BUILD SUCCESSFUL in 3m 33s（JDK 25 跑 Gradle，Jabel 输出 Java 8）
