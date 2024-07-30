package net.nicguzzo.wands.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.nicguzzo.wands.WandsExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;
import net.nicguzzo.wands.WandsMod;
import net.nicguzzo.wands.forge.claims.FTBChunks;
import net.nicguzzo.wands.forge.claims.Flan;
import net.nicguzzo.wands.forge.claims.Opac;

import java.nio.file.Path;

public class WandsExpectPlatformImpl {
    /**
     * This is our actual method to {@link WandsExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean claimCanInteract(ServerLevel level, BlockPos pos, Player player){
        WandsMod.LOGGER.info(" Chunk Protection canBreakBlock");
        if(WandsMod.has_opac){
            return Opac.canInteract(level,player,pos);
        }
        if(WandsMod.has_ftbchunks){
            return FTBChunks.canInteract(level,player,pos);
        }
        if(WandsMod.has_flan){
            return Flan.canInteract(level,player,pos);
        }
        return true;
    }
}
