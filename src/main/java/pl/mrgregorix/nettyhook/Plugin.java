package pl.mrgregorix.nettyhook;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.mrgregorix.nettyhook.hook.NettyHook;
import pl.mrgregorix.nettyhook.hook.PacketHandler;

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
}
