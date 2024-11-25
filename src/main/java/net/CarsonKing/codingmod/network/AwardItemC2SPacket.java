package net.CarsonKing.codingmod.network;

import net.CarsonKing.codingmod.ModItems.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AwardItemC2SPacket {
    private final int problemIndex;

    public AwardItemC2SPacket(int problemIndex) {
        this.problemIndex = problemIndex;
    }

    public static void encode(AwardItemC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.problemIndex);
    }

    public static AwardItemC2SPacket decode(FriendlyByteBuf buf) {
        return new AwardItemC2SPacket(buf.readInt());
    }

    public static void handle(AwardItemC2SPacket packet, ServerPlayer player) {
        if (player != null) {
            ItemStack rewardItem = getRewardItem(packet.problemIndex);
            if (rewardItem != null) {
                player.getInventory().add(rewardItem);
            }
        }
    }

    private static ItemStack getRewardItem(int index) {
        switch (index) {
            case 0:
                return new ItemStack(ModItems.PROBLEM_1_COMPLETE.get());
            case 1:
                return new ItemStack(ModItems.PROBLEM_2_COMPLETE.get());
            case 2:
                return new ItemStack(ModItems.PROBLEM_3_COMPLETE.get());
            case 3:
                return new ItemStack(ModItems.PROBLEM_4_COMPLETE.get());
            case 4:
                return new ItemStack(ModItems.PROBLEM_5_COMPLETE.get());
            case 5:
                return new ItemStack(ModItems.PROBLEM_6_COMPLETE.get());
            case 6:
                return new ItemStack(ModItems.PROBLEM_7_COMPLETE.get());
            case 7:
                return new ItemStack(ModItems.PROBLEM_8_COMPLETE.get());
            case 8:
                return new ItemStack(ModItems.PROBLEM_9_COMPLETE.get());
            case 9:
                return new ItemStack(ModItems.PROBLEM_10_COMPLETE.get());
            case 10:
                return new ItemStack(Items.DIAMOND);
            case 11:
                return new ItemStack(Items.NETHER_STAR);

            default:
                return null;
        }
    }
}