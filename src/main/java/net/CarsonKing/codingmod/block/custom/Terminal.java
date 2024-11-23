package net.CarsonKing.codingmod.block.custom;

import net.CarsonKing.codingmod.screen.TerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class Terminal extends Block {

    public Terminal(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            //Play sound on the client side
            level.playSound(player, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 1f);

            //Open the GUI on the client side
            Minecraft.getInstance().setScreen(new TerminalScreen());

            return InteractionResult.SUCCESS;
        } else {
            //Server-side logig
            return InteractionResult.CONSUME;
        }
    }
}



