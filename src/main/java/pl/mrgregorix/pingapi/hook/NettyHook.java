package pl.mrgregorix.pingapi.hook;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import pl.mrgregorix.pingapi.hook.init.InboundHandlerRegister;
import pl.mrgregorix.pingapi.hook.utils.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;

public final class NettyHook
{
    private static final Class                      mcServerClazz       = ReflectionUtils.getNMSClass("MinecraftServer");
    private static final Class                      connectionClass     = ReflectionUtils.getNMSClass("ServerConnection");
    private        final Object                     serverConnection;
    private              List<ChannelFuture>        channelFutures;
    private        final List<ChannelHandler>       ownHandlers         = Lists.newArrayList();
    private        final String                     name;
    private static       List<PacketHandler>        packetHandlers      = Lists.newArrayList();

    public NettyHook(String name)
    {
        this(name, false);
    }

    public NettyHook(String name, boolean prevenetSelfInit)
    {
        this.name = name;
        Object mcServer = ReflectionUtils.invokePrivateMethod(mcServerClazz, "getServer", null, new Class[0]);

        this.serverConnection = ReflectionUtils.getPrivateField(mcServerClazz, ReflectionUtils.byClass(mcServerClazz, ReflectionUtils.getNMSClass("ServerConnection")).getName(), mcServer);;

        if(!prevenetSelfInit)
            selfInit();
    }

    public static List<PacketHandler> getPacketHandlers()
    {
        return packetHandlers;
    }

    public void lookupForChannelFutures()
    {
        channelFutures = (List<ChannelFuture>) ReflectionUtils.getPrivateField(connectionClass, ReflectionUtils.byClass(connectionClass, List.class).getName(), serverConnection);
    }

    public List<ChannelFuture> getChannelFutures()
    {
        return channelFutures;
    }

    public void registerPacketListener(PacketHandler handler)
    {
        packetHandlers.add(handler);

    }

    public void registerPredefinedChannels()
    {
        register(new InboundHandlerRegister(name));
    }

    public void register(ChannelHandler handler)
    {
        ownHandlers.add(handler);
        for(ChannelFuture future : channelFutures)
        {
            future.channel().pipeline().addFirst("NettyHook|" + name, handler);

            System.out.println(handler + " registered to " + future.channel());

            for (Map.Entry<String, ChannelHandler> h : future.channel().pipeline())
            {
                System.out.println(h.getKey());
            }
        }
    }

    public void unregisterOwn()
    {
        for (ChannelHandler handler : ownHandlers)
            for (ChannelFuture future : channelFutures)
                future.channel().pipeline().remove(handler);
    }

    public void selfInit()
    {
        lookupForChannelFutures();
        registerPredefinedChannels();
    }
}
