# Form Demo

用于 Paper 1.21.11 子服的基岩版表单示例。通过 `/formdemo` 打开 `SimpleForm` 大厅菜单，再进入 `ModalForm` 确认弹窗或 `CustomForm` 偏好设置。

## 构建

使用 JDK 21。将部署时使用的 `floodgate-spigot.jar` 复制到本目录的 `libs/floodgate-spigot.jar`。该 JAR 由本地提供，Gradle 将它作为编译依赖。

在本目录执行 Windows 或 Linux / macOS 对应的命令。

```powershell
.\gradlew.bat build
```

```bash
sh ./gradlew build
```

生成的插件为 `build/libs/form-demo-1.0.0.jar`。

## 部署与验证

1. 将构建的插件放入已经安装 Floodgate 的 Paper 子服 `plugins/`。
2. 确认 Velocity 的 Floodgate 配置启用了 `send-floodgate-data: true`，代理和子服使用同一份 `key.pem`。
3. 重启相关进程后，用基岩版客户端经由 Velocity 进服并执行 `/formdemo`。
4. 分别体验确认、取消、提交偏好设置、留空昵称和关闭表单。偏好设置的结果会显示在聊天栏。
