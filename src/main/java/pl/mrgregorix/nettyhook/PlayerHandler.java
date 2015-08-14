package pl.mrgregorix.nettyhook;

import org.bukkit.entity.Player;
import pl.mrgregorix.nettyhook.hook.PacketHandler;

public class PlayerHandler extends PacketHandler
{
    private final Player player;

    PlayerHandler(Player player)
    {
        this.player = player;
    }

    @Override
    public void onPacketOut(Object packet, Player player) {}

    @Override
    public void onPacketIn(Object packet, Player player) {}

    public void sendPacket(Object packet)
    {
        super.channel.pipeline().writeAndFlush(packet);
    }
}
