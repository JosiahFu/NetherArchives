package archives.tater.netherarchives

import archives.tater.netherarchives.block.NetherArchivesBlocks
import archives.tater.netherarchives.block.entity.NetherArchivesBlockEntities
import archives.tater.netherarchives.entity.NetherArchivesEntities
import archives.tater.netherarchives.item.NetherArchivesItems
import archives.tater.netherarchives.modification.ModifyLootTables
import archives.tater.netherarchives.modification.ModifyWorldGen
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NetherArchives : ModInitializer {
    const val NAMESPACE = "netherarchives"

    @JvmField
    val logger: Logger = LoggerFactory.getLogger(NAMESPACE)

    val config = NetherArchivesConfig(true).load() as NetherArchivesConfig

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Eyes: {}", config.skeletonEyes)
        NetherArchivesBlocks.register()
        NetherArchivesBlockEntities.register()
        NetherArchivesItems.registerItemGroups()
        NetherArchivesEntities.register()
        ModifyWorldGen()
        ModifyLootTables()
    }

}
