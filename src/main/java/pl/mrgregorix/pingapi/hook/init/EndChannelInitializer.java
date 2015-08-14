package pl.mrgregorix.pingapi.hook.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import pl.mrgregorix.pingapi.hook.NettyHook;
import pl.mrgregorix.pingapi.hook.PacketHandler;


public class EndChannelInitializer extends ChannelInitializer<Channel>
{
    private static int          ID = 0;

    private final  String       name;
    private final  NettyHook    hook;

    public EndChannelInitializer(NettyHook hook, String name)
    {
        this.name = name;
        this.hook = hook;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception
    {
        for(PacketHandler handler : hook.getPacketHandlers())
            channel.pipeline().addBefore("packet_handler", "NettyHook|PH" + name + "|" + (ID++), handler);
    }
}
