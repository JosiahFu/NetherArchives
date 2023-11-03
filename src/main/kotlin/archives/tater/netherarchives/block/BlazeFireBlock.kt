package archives.tater.netherarchives.block

import net.minecraft.block.AbstractFireBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.GameRules
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

class BlazeFireBlock(settings: Settings) : AbstractFireBlock(settings, 2.0f) {
    companion object {
        val AGE: IntProperty = Properties.AGE_15

        private fun getFireTickDelay(random: Random) = 10 + random.nextInt(10)

    }

    init {
        defaultState = stateManager.defaultState.with(AGE, 0);
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    override fun isFlammable(state: BlockState?) = true

    @Suppress("OVERRIDE_DEPRECATION")
    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockPos = pos.down()
        return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (canPlaceAt(state, world, pos)) {
            return state;
        }
        return Blocks.AIR.defaultState
    }

    // Copied from FireBlock
    @Suppress("OVERRIDE_DEPRECATION")
    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        world.scheduleBlockTick(pos, this, getFireTickDelay(world.random))
        if (!world.gameRules.getBoolean(GameRules.DO_FIRE_TICK)) return

        val blockBelow = world.getBlockState(pos.down())
        val infiniburn = blockBelow.isIn(world.dimension.infiniburn())
        val age = state.get(AGE);
        
        if (!infiniburn && world.isRaining && random.nextFloat() < 0.2f + age * 0.015f) {
            world.removeBlock(pos, false)
            return
        }
        
        val newAge = (age + random.nextInt(3) / 2).coerceAtMost(15)

        if (age != newAge) {
            val newState = state.with(AGE, newAge)
            world.setBlockState(pos, newState, Block.NO_REDRAW)
        }
        
        if (!infiniburn && (!canPlaceAt(state, world, pos) || age > 6)) {
            world.removeBlock(pos, false)
            return;
        }

        BlockPos.iterateOutwards(pos, 1, 1, 1).forEach {
            if (world.getBlockState(it).block is BlazePowderBlock && world.random.nextFloat() > 0.5) {
                world.setBlockState(it, this.defaultState)
            }
        }

    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBlockAdded(
        state: BlockState,
        world: World,
        pos: BlockPos,
        oldState: BlockState,
        notify: Boolean
    ) {
        super.onBlockAdded(state, world, pos, oldState, notify)
        world.scheduleBlockTick(pos, this, getFireTickDelay(world.random))
    }

}
