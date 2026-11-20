package com.example.formdemo;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;

public final class FormDemoPlugin extends JavaPlugin {
    private static final List<String> THEMES = List.of("经典", "简洁");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendPlainMessage("请在游戏内使用 /formdemo。");
            return true;
        }
        if (!FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            player.sendPlainMessage("请使用基岩版客户端体验表单。");
            return true;
        }

        openMenu(player);
        return true;
    }

    private void openMenu(Player player) {
        send(player, SimpleForm.builder()
            .title("大厅菜单")
            .content("选择一个功能")
            .button("确认弹窗")
            .button("偏好设置")
            .validResultHandler(response -> {
                int selected = response.clickedButtonId();
                onMainThread(player, () -> {
                    switch (selected) {
                        case 0 -> openConfirmation(player);
                        case 1 -> openPreferences(player);
                    }
                });
            })
            .build());
    }

    private void openConfirmation(Player player) {
        send(player, ModalForm.builder()
            .title("查看大厅提示")
            .content("要在聊天栏显示大厅提示吗？")
            .button1("显示提示")
            .button2("取消")
            .validResultHandler(response -> {
                String message = response.clickedFirst()
                    ? "大厅提示：输入 /formdemo 可以再次打开菜单。"
                    : "已取消。";
                onMainThread(player, () -> player.sendPlainMessage(message));
            })
            .build());
    }

    private void openPreferences(Player player) {
        send(player, CustomForm.builder()
            .title("偏好设置")
            .label("提交后会在聊天栏显示填写结果。")
            .input("昵称", "请输入 1 到 16 个字符")
            .dropdown("界面风格", THEMES)
            .toggle("接收大厅提示", true)
            .slider("提示音量", 0, 10, 1, 5)
            .validResultHandler(response -> {
                String input = response.asInput();
                String nickname = input == null ? "" : input.strip();
                int themeIndex = response.asDropdown();
                boolean showTips = response.asToggle();
                float volume = response.asSlider();

                if (nickname.isEmpty() || nickname.codePointCount(0, nickname.length()) > 16) {
                    onMainThread(player, () -> player.sendPlainMessage("昵称须为 1 到 16 个字符。"));
                    return;
                }
                if (themeIndex < 0 || themeIndex >= THEMES.size() || !(volume >= 0 && volume <= 10)) {
                    onMainThread(player, () -> player.sendPlainMessage("设置值超出范围，请重新填写。"));
                    return;
                }

                String message = "昵称 %s，风格 %s，提示 %s，音量 %s"
                    .formatted(nickname, THEMES.get(themeIndex), showTips ? "开启" : "关闭", volume);
                onMainThread(player, () -> player.sendPlainMessage(message));
            })
            .closedResultHandler(() -> onMainThread(player,
                () -> player.sendPlainMessage("已关闭偏好设置。")))
            .invalidResultHandler(() -> onMainThread(player,
                () -> player.sendPlainMessage("无法读取表单响应，请重新打开。")))
            .build());
    }

    private void send(Player player, Form form) {
        if (!FloodgateApi.getInstance().sendForm(player.getUniqueId(), form)) {
            player.sendPlainMessage("表单发送失败，请稍后重试。");
        }
    }

    private void onMainThread(Player player, Runnable action) {
        getServer().getScheduler().runTask(this, () -> {
            if (player.isOnline()) {
                action.run();
            }
        });
    }
}
