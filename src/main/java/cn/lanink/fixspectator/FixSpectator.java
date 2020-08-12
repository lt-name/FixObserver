package cn.lanink.fixspectator;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.PluginBase;

/**
 * @author lt_name
 */
public class FixSpectator extends PluginBase implements Listener {

    public static final String VERSION = "?";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("VERSION: " + VERSION +"  加载完成");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDataPacketSend(DataPacketSendEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPacket() instanceof AdventureSettingsPacket) {
            AdventureSettingsPacket packet = (AdventureSettingsPacket) event.getPacket();
            Player player = event.getPlayer();
            if (player.isSpectator()) {
                event.setCancelled(true);
                AdventureSettingsPacket pk = new AdventureSettingsPacket();
                for (AdventureSettings.Type t : AdventureSettings.Type.values()) {
                    pk.setFlag(t.getId(), player.getAdventureSettings().get(t));
                }
                pk.commandPermission = packet.commandPermission;
                pk.playerPermission = Player.PERMISSION_VISITOR;
                pk.entityUniqueId = player.getId();
                event.getPlayer().dataPacket(pk);
                getLogger().info(pk.toString());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPacket() instanceof LevelSoundEventPacket ||
                event.getPacket() instanceof LevelSoundEventPacketV1 ||
                event.getPacket() instanceof LevelSoundEventPacketV2) {
            Player player = event.getPlayer();
            if (player.getGamemode() == 3) {
                player.dataPacket(event.getPacket());
                event.setCancelled(true);
            }
        }
    }

}
