package net.CarsonKing.codingmod.network;

import net.CarsonKing.codingmod.codingmod;
import net.CarsonKing.codingmod.network.AwardItemC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;

public class ModMessages {
    private static final Map<Class<?>, PacketHandler<?>> PACKET_HANDLERS = new HashMap<>();
    private static int packetId = 0;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModMessages::setup);
    }

    private static void setup(final FMLCommonSetupEvent event) {
        registerPackets();
    }

    private static void registerPackets() {
        registerPacket(
                AwardItemC2SPacket.class,
                AwardItemC2SPacket::encode,
                AwardItemC2SPacket::decode,
                AwardItemC2SPacket::handle
        );
    }

    public static <T> void registerPacket(
            Class<T> clazz,
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, ServerPlayer> handler
    ) {
        PACKET_HANDLERS.put(clazz, new PacketHandler<>(encoder, decoder, handler));
        packetId++;
    }

    public static <T> void sendToServer(T message, FriendlyByteBuf buf) {
        PacketHandler<T> handler = (PacketHandler<T>) PACKET_HANDLERS.get(message.getClass());
        if (handler != null) {
            buf.clear();
            handler.encode(message, buf);
            // Send the data to the server (implementation depends on your networking setup)
        }
    }

    public static <T> void sendToClient(T message, ServerPlayer player, FriendlyByteBuf buf) {
        PacketHandler<T> handler = (PacketHandler<T>) PACKET_HANDLERS.get(message.getClass());
        if (handler != null) {
            buf.clear();
            handler.encode(message, buf);
            // Send the data to the specific client (implementation depends on your networking setup)
        }
    }

    private static class PacketHandler<T> {
        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, ServerPlayer> handler;

        public PacketHandler(
                BiConsumer<T, FriendlyByteBuf> encoder,
                Function<FriendlyByteBuf, T> decoder,
                BiConsumer<T, ServerPlayer> handler
        ) {
            this.encoder = encoder;
            this.decoder = decoder;
            this.handler = handler;
        }

        public void encode(T message, FriendlyByteBuf buf) {
            encoder.accept(message, buf);
        }

        public T decode(FriendlyByteBuf buf) {
            return decoder.apply(buf);
        }

        public void handle(T message, ServerPlayer player) {
            handler.accept(message, player);
        }
    }
}
