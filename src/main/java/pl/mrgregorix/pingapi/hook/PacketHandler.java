package pl.mrgregorix.pingapi.hook;

import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import pl.mrgregorix.pingapi.hook.utils.ReflectionUtils;

import java.util.Arrays;
import java.util.UUID;

@ChannelHandler.Sharable
public abstract class PacketHandler extends ChannelDuplexHandler implements Listener
{
    private static final    Class       successLoginClass = ReflectionUtils.getNMSClass("PacketLoginOutSuccess");
    private                 Player      player;
    private                 UUID        uuid;
    private                 Class[]     inFilterList;
    private                 Class[]     outFilterList;

    public final void setInFilter(String... filter)
    {
        inFilterList = new Class[filter.length];
        for(int i = 0 ; i < filter.length ; i++)
        {
            String packetName = filter[i];

            Validate.isTrue(packetName.startsWith("Packet"), packetName + " isn't a packet!");

            Class packetClass = ReflectionUtils.getNMSClass(packetName);
            Validate.notNull(packetClass, "Unknown packet " + packetName);

            inFilterList[i] = packetClass;
        }
    }

    public final void setOutFilter(String... filter)
    {
        outFilterList = new Class[filter.length];
        for(int i = 0 ; i < filter.length ; i++)
        {
            String packetName = filter[i];

            Validate.isTrue(packetName.startsWith("Packet"), packetName + " isn't a packet!");

            Class packetClass = ReflectionUtils.getNMSClass(packetName);
            Validate.notNull(packetClass, "Unknown packet " + packetName);

            outFilterList[i] = packetClass;
        }
    }

    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if(inFilterList == null || inFilterList.length <= 0 || Arrays.asList(inFilterList).contains(msg.getClass()))
            onPacketIn(msg, player);

        super.channelRead(ctx, msg);
    }

    @EventHandler
    public final void onPlayerLogin(PlayerLoginEvent event)
    {
        if (event.getPlayer().getUniqueId().equals(uuid))
        {
            this.player = event.getPlayer();
        }
    }

    @Override
    public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if(successLoginClass.isInstance(msg))
        {
            GameProfile profile = (GameProfile) ReflectionUtils.getPrivateField(successLoginClass, ReflectionUtils.byClass(successLoginClass, GameProfile.class).getName(), msg);

            uuid = profile.getId();
        }

        if (uuid != null && player == null)
        {
            player = Bukkit.getPlayer(uuid);
        }

        if(outFilterList == null || outFilterList.length <= 0 || Arrays.asList(outFilterList).contains(msg.getClass()))
            onPacketOut(msg, player);

        super.write(ctx, msg, promise);
    }

    public abstract void onPacketOut(Object packet, Player player);
    public abstract void onPacketIn(Object packet, Player player);
}
