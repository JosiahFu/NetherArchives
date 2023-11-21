package archives.tater.netherarchives.entity

import archives.tater.netherarchives.block.BlazePowderBlock
import archives.tater.netherarchives.block.NetherArchivesBlocks
import archives.tater.netherarchives.item.NetherArchivesItems
import archives.tater.netherarchives.listCopy
import archives.tater.netherarchives.shuffled
import net.minecraft.entity.EntityType
import net.minecraft.entity.FallingBlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.Item
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class BlazeLanternEntity : ThrownItemEntity {
    constructor(type: EntityType<BlazeLanternEntity>, world: World) : super(type, world)
    constructor(world: World, owner: LivingEntity) : super(NetherArchivesEntities.BLAZE_LANTERN, owner, world)
    constructor(world: World, x: Double, y: Double, z: Double) : super(NetherArchivesEntities.BLAZE_LANTERN, x, y, z, world)

    override fun getDefaultItem(): Item = NetherArchivesItems.BLAZE_LANTERN

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (world.isClient) return
        val pos = hitResult.pos
        val blockPos = BlockPos(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())
        world.playSound(null, blockPos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.5f, 1.0f)
        world.playSound(null, blockPos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 1.0f, 0.6f)
        BlockPos.iterateInSquare(blockPos, 1, Direction.NORTH, Direction.EAST).listCopy()
            .filter {
                val blockState = world.getBlockState(it)
                blockState.block is BlazePowderBlock ||
                        (blockState.isAir &&
                                (world.getBlockState(it.down()).isAir ||
                                NetherArchivesBlocks.BLAZE_FIRE.canPlaceAt(blockState, world, it)))
            }
            .shuffled(world.random)
            .toMutableList().run {
                (if (remove(blockPos)) listOf(blockPos) else emptyList()) + take(4)
            }
            .forEach {
                if (world.getBlockState(it.down()).isAir) {
                    FallingBlockEntity.spawnFromBlock(world, it, NetherArchivesBlocks.BLAZE_FIRE.defaultState)
                } else {
                    world.setBlockState(it, NetherArchivesBlocks.BLAZE_FIRE.defaultState)
                }
            }
        discard()
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        entityHitResult.entity.setOnFireFor(10)
    }
}