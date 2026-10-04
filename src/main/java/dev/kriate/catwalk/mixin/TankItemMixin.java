package dev.kriate.catwalk.mixin;
import com.simibubi.create.content.fluids.tank.*;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import dev.kriate.catwalk.StorageAxes;
import net.minecraft.core.*;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=FluidTankItem.class,remap=false)
public abstract class TankItemMixin extends BlockItem {
    protected TankItemMixin(Block block,Item.Properties properties){super(block,properties);}
    @Inject(method="tryMultiPlace",at=@At("HEAD"),cancellable=true)
    private void morefix$layer(BlockPlaceContext ctx,CallbackInfo ci) {
        ci.cancel();
        var player=ctx.getPlayer();
        if(player==null || player.isShiftKeyDown() || SymmetryWandItem.presentInHotbar(player))return;
        var world=ctx.getLevel(); var face=ctx.getClickedFace();
        var placed=ctx.getClickedPos(); var against=placed.relative(face.getOpposite());
        var state=world.getBlockState(against); Axis axis=StorageAxes.axis(state);
        if(state.getBlock()!=getBlock() || face.getAxis()!=axis)return;
        if(!(world.getBlockEntity(against) instanceof FluidTankBlockEntity part))return;
        var controller=part.getControllerBE(); if(controller==null || controller.getWidth()<=1)return;
        int width=controller.getWidth();
        int end=face.getAxisDirection()==Direction.AxisDirection.NEGATIVE?-1:controller.getHeight();
        var start=StorageAxes.offset(controller.getBlockPos(),axis,0,end,0);
        if(axis.choose(start.getX(),start.getY(),start.getZ())!=axis.choose(placed.getX(),placed.getY(),placed.getZ()))return;
        int needed=0;
        for(int u=0;u<width;u++)for(int v=0;v<width;v++) {
            var pos=StorageAxes.offset(start,axis,u,0,v);var current=world.getBlockState(pos);
            if(current.getBlock()==getBlock() && StorageAxes.axis(current)==axis)continue;
            if(!current.canBeReplaced())return;
            needed++;
        }
        if(!player.isCreative() && ctx.getItemInHand().getCount()<needed)return;
        boolean wasSilent=((net.neoforged.neoforge.common.extensions.IEntityExtension)player).getPersistentData().getBoolean("SilenceTankSound");
        try {
            ((net.neoforged.neoforge.common.extensions.IEntityExtension)player).getPersistentData().putBoolean("SilenceTankSound",true);
            for(int u=0;u<width;u++)for(int v=0;v<width;v++) {
                var pos=StorageAxes.offset(start,axis,u,0,v);var current=world.getBlockState(pos);
                if(current.getBlock()==getBlock() && StorageAxes.axis(current)==axis)continue;
                super.place(BlockPlaceContext.at(ctx,pos,face));
            }
        } finally {
            if(wasSilent)((net.neoforged.neoforge.common.extensions.IEntityExtension)player).getPersistentData().putBoolean("SilenceTankSound",true);
            else ((net.neoforged.neoforge.common.extensions.IEntityExtension)player).getPersistentData().remove("SilenceTankSound");
        }
    }
}

