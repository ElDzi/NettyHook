package pl.mrgregorix.nettyhook;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.mrgregorix.nettyhook.hook.NettyHook;
import pl.mrgregorix.nettyhook.hook.PacketHandler;
import pl.mrgregorix.nettyhook.hook.utils.ReflectionUtils;

public class Plugin extends JavaPlugin implements Listener
{
    private NettyHook debuggerHook;

    @Override
    public void onEnable()
    {
        if(getConfig().getBoolean("debug"))
        {
            debuggerHook = new NettyHook("NettyHookLIB|Debug");
            System.out.println("Debbugging " + debuggerHook);

            debuggerHook.registerPacketListener(new PacketHandler()
            {
                @Override
                public void onPacketOut(Object packet, Player player)
                {
                    System.out.println("Out packet (Player: " + (player != null ? player.getName() : "undefined") + "): " + packet.getClass().getName());
                }

                @Override
                public void onPacketIn(Object packet, Player player)
                {
                    System.out.println("In packet (Player: " + (player != null ? player.getName() : "undefined") + "): " + packet.getClass().getName());
                }
            });
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    @Override
    public void onDisable()
    {
        debuggerHook.unregisterOwn();
    }

    @EventHandler
    public void onPlayerCmd(PlayerCommandPreprocessEvent event)
    {
        PacketPlayOutChat chat = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(
                        "[" +
                            "{text: \"Debug \", color: \"green\"}," +
                            "{text: \"Lubie w Pupe\", color: \"red\"}" +
                        "]"
        ));

        debuggerHook.getPlayerChannelManager().getPlayerHandler(event.getPlayer()).sendPacket(chat);
    }
}
