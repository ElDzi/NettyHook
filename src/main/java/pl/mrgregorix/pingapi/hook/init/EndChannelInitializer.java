package pl.mrgregorix.pingapi.hook.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import pl.mrgregorix.pingapi.hook.NettyHook;
import pl.mrgregorix.pingapi.hook.PacketHandler;


public class EndChannelInitializer extends ChannelInitializer<Channel>
{
    private static int ID = 0;

    private final  String name;

    public EndChannelInitializer(String name)
    {
        this.name = name;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception
    {
        for(PacketHandler handler : NettyHook.getPacketHandlers())
            channel.pipeline().addBefore("packet_handler", "NettyHook|PH" + name + "|" + (ID++), handler);
    }
}
