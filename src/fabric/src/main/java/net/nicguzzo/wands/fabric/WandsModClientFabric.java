package net.nicguzzo.wands.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.nicguzzo.wands.WandsMod;
import net.nicguzzo.wands.client.WandsModClient;

import java.util.Optional;


public class WandsModClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WandsModClient.initialize();
        Optional<ModContainer> cont= FabricLoader.getInstance().getModContainer("optifabric");
        if(cont.isPresent()){
            WandsModClient.has_optifine=true;
            //WandsMod.log("has optifine!!!!!!!!!!!!",true);
        }
        Optional<ModContainer> opac= FabricLoader.getInstance().getModContainer("openpartiesandclaims");
         if(opac.isPresent()){
            WandsModClient.has_opac=true;
            //WandsMod.log("cli has opac!!!!!!!!!!!!",true);
        }else{
             //WandsMod.log("cli NO opac!!!!!!!!!!!!",true);
         }
    }
}