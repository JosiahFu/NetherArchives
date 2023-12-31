package archives.tater.netherarchives.modification

import archives.tater.netherarchives.NetherArchives
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.gen.GenerationStep


object ModifyWorldGen {
    private val MAGNETITE_DELTA =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier(NetherArchives.NAMESPACE, "magnetite_delta"))
    private val MAGNETITE_BLOBS =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier(NetherArchives.NAMESPACE, "magnetite_blobs"))

    operator fun invoke() {
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(BiomeKeys.BASALT_DELTAS),
            GenerationStep.Feature.SURFACE_STRUCTURES,
            MAGNETITE_DELTA
        )
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(BiomeKeys.BASALT_DELTAS),
            GenerationStep.Feature.SURFACE_STRUCTURES,
            MAGNETITE_BLOBS
        )
    }
}
