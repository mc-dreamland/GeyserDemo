package com.example.rpcdemo;

import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.module.PluginMessageModule;
import org.msgpack.core.MessagePackException;

public final class RpcDemoPlugin extends JavaPlugin implements PluginMessageListener {
    private static final String CHANNEL = "floodgate:netease";

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (!CHANNEL.equals(channel) || !player.isOnline()
                || !FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            return;
        }

        try {
            String requestId = RpcPacket.readRequest(data);
            if (requestId == null) {
                return;
            }

            byte[] reply = RpcPacket.writeReply(requestId);
            if (PluginMessageModule.neteaseCustomChannel.sendPacket(player.getUniqueId(), reply)) {
                getLogger().info("QueryServerMessage -> ServerMessage (" + requestId + ")");
            } else {
                getLogger().warning("Could not send ServerMessage (" + requestId + ")");
            }
        } catch (IOException | MessagePackException exception) {
            getLogger().fine("Could not decode or encode the PythonRPC demo message.");
        }
    }
}
