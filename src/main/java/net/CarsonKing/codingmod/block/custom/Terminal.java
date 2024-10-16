package net.CarsonKing.codingmod.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;


//Creates the terminal block
public class Terminal extends Block {
    public Terminal(Properties properties) {
        super(properties);
    }

    //Allows the block to be right clicked
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {

        pLevel.playSound(pPlayer, pPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 1f);
        return InteractionResult.SUCCESS;
    }
}
