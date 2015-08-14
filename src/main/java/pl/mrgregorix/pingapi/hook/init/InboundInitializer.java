package pl.mrgregorix.pingapi.hook.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import pl.mrgregorix.pingapi.hook.NettyHook;

public class InboundInitializer extends ChannelInitializer<Channel>
{
    private final String    name;
    private final NettyHook hook;


    public InboundInitializer(String name, NettyHook hook)
    {
        this.name = name;
        this.hook = hook;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception
    {
        channel.pipeline().addLast(new EndChannelInitializer(hook, name));
    }
}
