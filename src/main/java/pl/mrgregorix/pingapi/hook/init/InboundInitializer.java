package pl.mrgregorix.pingapi.hook.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class InboundInitializer extends ChannelInitializer<Channel>
{
    private final String name;

    public InboundInitializer(String name)
    {
        this.name = name;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception
    {
        channel.pipeline().addLast(new EndChannelInitializer(name));
    }
}
