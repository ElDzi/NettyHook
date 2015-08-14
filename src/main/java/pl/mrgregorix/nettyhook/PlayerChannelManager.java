package pl.mrgregorix.nettyhook;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.mrgregorix.nettyhook.hook.NettyHook;
import pl.mrgregorix.nettyhook.hook.PacketHandler;
import pl.mrgregorix.nettyhook.hook.init.InboundHandlerRegister;
import pl.mrgregorix.nettyhook.hook.init.InboundInitializer;
import pl.mrgregorix.nettyhook.hook.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerChannelManager extends PacketHandler
{
    private static final Class                          PACKET_CLASS    = ReflectionUtils.getNMSClass("PacketLoginOutSuccess");
    private static final HashMap<UUID, PlayerHandler>   playerHandlers  = Maps.newHashMap();
    private        final NettyHook                      hook;

    public PlayerChannelManager(NettyHook hook)
    {
        this.hook = hook;
        setOutFilter("PacketLoginOutSuccess");
    }

    @Override
    public void onPacketOut(Object packet, Player player)
    {
        GameProfile profile = (GameProfile) ReflectionUtils.getPrivateField(PACKET_CLASS, ReflectionUtils.byClass(PACKET_CLASS, GameProfile.class).getName(), packet);

        if(playerHandlers.containsKey(profile.getId()))
            playerHandlers.remove(profile.getId());

        PlayerHandler handler =  new PlayerHandler(player);
        handler.setChannel(super.channel);

        playerHandlers.put(profile.getId(), handler);
    }

    @Override
    public void onPacketIn(Object packet, Player player) {}

    public PlayerHandler getPlayerHandler(Player player)
    {
        return getPlayerHandler(player.getUniqueId());
    }

    public PlayerHandler getPlayerHandler(UUID uniqueId)
    {
        Player player = null;
        PlayerHandler handler;

        return ((handler = playerHandlers.get(uniqueId)) != null && (player = Bukkit.getPlayer(uniqueId)) != null) ? handler : retrievePlayerHandler(player);
    }

    private static final Class  ENTITY_PLAYER_CLASS          = ReflectionUtils.getBukkitClass("EntityPlayer");
    private static final Class  PLAYER_CONNECTION_CLASS      = ReflectionUtils.getNMSClass("PlayerConnection");
    private static final Class  NETWORK_MANAGER_CLASS        = ReflectionUtils.getNMSClass("NetworkManager");
    private static final String CHANNEL_FIELD                = ReflectionUtils.byClass(NETWORK_MANAGER_CLASS, Channel.class).getName();


    private PlayerHandler retrievePlayerHandler(Player player)
    {
        if(player == null)
            return null;

        Object entityPlayer         = ReflectionUtils.getHandle(player);
        Object playerConnection     = ReflectionUtils.getPrivateField(ENTITY_PLAYER_CLASS, "playerConnection", entityPlayer);
        Object networkManager       = ReflectionUtils.getPrivateField(PLAYER_CONNECTION_CLASS, "networkManager", playerConnection);
        Channel channel             = (Channel) ReflectionUtils.getPrivateField(NETWORK_MANAGER_CLASS, CHANNEL_FIELD, networkManager);

        PlayerHandler handler = new PlayerHandler(player);

        channel.pipeline().addFirst(new InboundInitializer("PlayerHandler|" + player.getUniqueId().toString(), hook));

        handler.setChannel(channel);

        return handler;
    }
}
