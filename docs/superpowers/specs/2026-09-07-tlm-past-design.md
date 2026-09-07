# TouhouLittleMaid: Past 设计说明

日期：2026-09-07  
状态：本轮已按该设计落地骨架（父代理授权直接实现，不卡审批门）。

## 问题

把 TartaricAcid/TouhouLittleMaid（Forge 1.20.1）的功能迁到 Forge 1.7.10。不能复制粘贴编译。

## 决策

- **工程**：GTNH ExampleMod1.7.10 / RetroFuturaGradle，不用已失效的古代 ForgeGradle。
- **身份**：`modid=touhou_little_maid`，包名与上游一致，显示名 `TouhouLittleMaid: Past`。
- **实体**：`EntityMaid extends EntityTameable`，尺寸 0.6×1.5。
- **召唤**：御币 / 初始硬盘 / 刷怪蛋点地生成已驯服女仆（祭坛仪式后置）。
- **AI**：原版 EntityAI。坐下=待机，站立=跟随；`attack` 任务保护主人。
- **渲染**：`RenderBiped` + 64×32 占位皮肤。不做 GeckoLib。
- **任务**：枚举保留全部 1.20.1 id；未实现的只登记。
- **资源**：本仓库不提交 `_ref` 上游整仓。许可证 MIT + CC BY-NC-SA 4.0。

## 非目标（本轮）

GUI、模型包、音游、多方块祭坛、现代模组兼容、提交/推送 git（除非远程创建需要）。
