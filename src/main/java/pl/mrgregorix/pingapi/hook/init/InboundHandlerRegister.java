package pl.mrgregorix.pingapi.hook.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundHandlerRegister extends ChannelInboundHandlerAdapter
{
    private final String name;


    public InboundHandlerRegister(String name)
    {
        this.name = name;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        Channel c = ((Channel)msg);

        c.pipeline().addFirst(name, new InboundInitializer(name));

        ctx.fireChannelRead(msg);
    }
}
