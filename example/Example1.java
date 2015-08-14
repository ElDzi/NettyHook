/*
 * Example 1:
 * Redirect all chat messages to action bar
 */

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.mrgregorix.pingapi.hook.NettyHook;
import pl.mrgregorix.pingapi.hook.PacketHandler;
import pl.mrgregorix.pingapi.hook.utils.ReflectionUtils;

public class Example1 extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        NettyHook hook = new NettyHook("Example1");

        PacketHandler handler = new PacketHandler()
        {
            @Override
            public void onPacketOut(Object packet, Player player)
            {
                ReflectionUtils.setPrivateField(ReflectionUtils.getNMSClass("PacketPlayOutChat"), "b", packet, (byte)2);
            }

            @Override
            public void onPacketIn(Object packet, Player player)
            {

            }
        };

        handler.setOutFilter("PacketPlayOutChat");

        hook.registerPacketListener(handler);
    }
}
