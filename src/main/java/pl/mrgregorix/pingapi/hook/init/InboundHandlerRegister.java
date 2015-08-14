package pl.mrgregorix.pingapi.hook.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import pl.mrgregorix.pingapi.hook.NettyHook;

public class InboundHandlerRegister extends ChannelInboundHandlerAdapter
{
    private final String    name;
    private final NettyHook hook;

    public InboundHandlerRegister(String name, NettyHook hook)
    {
        this.name = name;
        this.hook = hook;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        Channel c = ((Channel)msg);

        c.pipeline().addFirst(name, new InboundInitializer(name, hook));

        ctx.fireChannelRead(msg);
    }
}
