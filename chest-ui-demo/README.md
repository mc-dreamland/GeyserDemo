# Chest UI Demo

MoreGui 的钓鱼背包贴图和箱子界面示例。资源包按标题里的 `%cgt_auto_dy_backpack` 选择面板，单箱使用 27 格，双箱使用 54 格。物品存放、拿取与拖拽沿用原版箱子，运行示例只需这个基岩资源包。

## 打包资源包

在本目录执行以下 PowerShell 命令，产物的清单位于 ZIP 根目录。

```powershell
New-Item -ItemType Directory -Force -Path .\build | Out-Null
Compress-Archive -Path .\resource-pack\* -DestinationPath .\build\ChestUiDemo.zip -Force
```

将 `build/ChestUiDemo.zip` 放入中国版 Geyser 数据目录的 `packs/ResourcePacks/`，执行 `geyser loadpacks`，让基岩客户端退出后重新进服。如果同时加载了 MoreGui 或其他箱子 UI 资源包，可在测试服只启用这一份箱子界面覆盖来核对效果。

## 领取、放置并打开箱子

在 **Java 子服**内用有命令权限的玩家执行。命令使用 Java 1.21.5 起的文本组件语法，本项目按 Paper 1.21.11 验证。箱子放下时会保留物品的自定义名称，打开时该名字进入容器标题。若从控制台执行，用玩家名替换 `@s`。

单个箱子放在空地，打开后触发 27 格面板。

```mcfunction
/give @s minecraft:chest[minecraft:custom_name={text:"%cgt_auto_dy_backpack",italic:false}] 1
```

领取两个同名箱子，正常并排放置并确认已合并，再打开查看 54 格面板。合并时保持朝向一致，并让两半都带同一个标记。

```mcfunction
/give @s minecraft:chest[minecraft:custom_name={text:"%cgt_auto_dy_backpack",italic:false}] 2
```

把 `<x> <y> <z>` 换成箱子的实际坐标，可以在 Java 子服核对方块保存的名字。

```mcfunction
/data get block <x> <y> <z> CustomName
```

测试时试一下首尾槽位、从背包移入和重新打开。再打开一个普通箱子，确认仍显示默认面板。

## 文件对应关系

| 文件 | 作用 |
| --- | --- |
| `resource-pack/ui/_global_variables.json` | 声明标题标记 |
| `resource-pack/ui/chest_screen.json` | 给大小箱子分别选择自定义面板 |
| `resource-pack/ui/fishing.json` | 复用 MoreGui 面板，按 27 或 54 格绑定真实箱子槽位 |
| `resource-pack/textures/ui/fishing/dy_backpack.png` | MoreGui 原始钓鱼背包贴图 |
