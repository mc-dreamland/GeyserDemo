# PythonRPC Tip Demo

行为包的 MOD SDK 脚本仅支持 Python 2.7。进服后，`SetTipMessage` 会在快捷栏上方显示提示，`SetLeftCornerNotify` 会在玩家客户端的左上角显示聊天通知。行为包在本地玩家加载完成后发起请求，Paper 回传 `tipKey: welcome`，客户端再从资源包查找文字并显示。

| 目录 | 内容 |
| --- | --- |
| `packs/behavior_packs/RpcDemo_BP/` | 行为包、MOD SDK 入口和客户端系统 |
| `packs/resource_packs/RpcDemo_RP/` | 资源包和 `modconfigs/geyser_rpc_demo.json` 提示文案 |
| `src/main/` | Paper 插件和 PythonRPC 编解码 |

## 构建 Paper 插件

使用 JDK 21。将配套 NetEase Floodgate 分支构建的 `floodgate-spigot.jar` 放到本项目的 `libs/` 中。Gradle 用它编译，Paper 通过 `plugin.yml` 的 `libraries` 加载 MessagePack。

```powershell
.\gradlew.bat build
```

```bash
sh ./gradlew build
```

将生成的 `build/libs/pythonrpc-demo-1.0.0.jar` 放入 Paper 1.21.11 子服的 `plugins/`。在 Velocity 的 Floodgate 配置中启用 `send-floodgate-data: true`，并让 Velocity 和 Paper 使用同一份 `key.pem`。[配套 Geyser](https://github.com/mc-dreamland/Geyser/tree/netease/1.21.130)、[配套 Floodgate](https://github.com/mc-dreamland/FloodgateP/tree/netease_2.0)

## 打包和自测

1. 把 `packs/` 中的 `RpcDemo_RP/` 与 `RpcDemo_BP/` 原样放进网易服务器预下载模板的 `resource_packs/` 与 `behavior_packs/`。两个包的 `manifest.json` 都直接位于各自的包根目录。保留模板中的 `levelname.txt` 和空行为包。
2. 压缩整个预下载模板目录，在网易服务器编辑界面选择适用版本并上传 ZIP。通过官方自测流程，用目标中国版客户端经 Velocity 进入安装了示例插件的子服。
3. 进服后，快捷栏上方应短暂显示「欢迎进入服务器！PythonRPC 联通成功。」左上角也应出现带 `[RpcDemo]` 前缀的聊天通知。Paper 日志应出现 `QueryServerMessage -> ServerMessage (demo-1)`，客户端脚本日志应出现 `[RpcDemo] Displayed tip: welcome`。
4. 自测通过后，从服务器的「更多操作 → 包体下载」获取加密 ZIP，部署到 Geyser 数据目录的 `packs/Encryption/`。确认 `netease.use-encrypted-pack-directories` 已启用，重载包后重新进服验证。

Paper 已记录回复时，可查客户端脚本日志中的 `Tip missing in resource pack`。出现这条日志时，检查 RP 是否随包体分发，以及配置文件路径和 `welcome` 键是否匹配。要修改提示，只需编辑资源包的 JSON，更新 RP 版本和 BP 内的依赖版本，再重新打包上传。

[Python 脚本开发入门](https://github.com/MCNeteaseDevs/netease-bedrock-wiki/blob/main/mcguide/20-%E7%8E%A9%E6%B3%95%E5%BC%80%E5%8F%91/13-%E6%A8%A1%E7%BB%84SDK%E7%BC%96%E7%A8%8B/2-Python%E8%84%9A%E6%9C%AC%E5%BC%80%E5%8F%91/0-%E8%84%9A%E6%9C%AC%E5%BC%80%E5%8F%91%E5%85%A5%E9%97%A8.md) · [SetTipMessage](https://github.com/MCNeteaseDevs/mc-netease-sdk/blob/main/1-ModAPI/接口/世界/消息.md#settipmessage) · [SetLeftCornerNotify](https://github.com/MCNeteaseDevs/mc-netease-sdk/blob/main/1-ModAPI/接口/世界/消息.md#setleftcornernotify) · [GetModConfigJson](https://github.com/MCNeteaseDevs/mc-netease-sdk/blob/main/1-ModAPI/接口/通用/工具.md#getmodconfigjson)
