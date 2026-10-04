package dev.kriate.catwalk.mixin;

import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.api.boiler.BoilerHeater;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value=BoilerData.class,remap=false)
public abstract class BoilerAxesMixin {
    @Shadow public boolean passiveHeat;
    @Shadow public int activeHeat;
    @Shadow public boolean needsHeatLevelUpdate;
    @Redirect(method={"evaluate","checkPipeOrganAdvancement"},at=@At(value="INVOKE",target="Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"))
    private BlockPos morefix$parts(BlockPos origin,int x,int y,int z,FluidTankBlockEntity controller) {
        return StorageAxes.offset(origin,controller.getMainConnectionAxis(),x,y,z);
    }
    @Inject(method="updateTemperature",at=@At("HEAD"),cancellable=true)
    private void morefix$heat(FluidTankBlockEntity controller,CallbackInfoReturnable<Boolean> cir) {
        Axis axis=controller.getMainConnectionAxis();if(axis==Axis.Y)return;
        boolean beforePassive=passiveHeat;int beforeActive=activeHeat;
        needsHeatLevelUpdate=false;passiveHeat=false;activeHeat=0;
        int width=controller.getWidth(),length=controller.getHeight();
        int sx=StorageAxes.size(axis,Axis.X,width,length),sz=StorageAxes.size(axis,Axis.Z,width,length);
        var level=controller.getLevel();var origin=controller.getBlockPos();
        for(int x=0;x<sx;x++)for(int z=0;z<sz;z++) {
            var pos=origin.offset(x,-1,z);float heat=BoilerHeater.findHeat(level,pos,level.getBlockState(pos));
            if(heat==0)passiveHeat=true;else if(heat>0)activeHeat+=(int)heat;
        }
        passiveHeat &= activeHeat==0;
        cir.setReturnValue(beforePassive!=passiveHeat || beforeActive!=activeHeat);
    }
}
