package dev.kriate.catwalk.client;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import fi.dy.masa.litematica.materials.*;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.malilib.util.ItemType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

/** Loaded only by the optional Forgematica mixins. */
public final class ForgematicaMaterials {
    private record Coating(ItemStack item,CompoundTag material) {}
    private final Map<ItemType,int[]> counts=new LinkedHashMap<>();
    private void add(ItemStack stack,boolean missing,boolean mismatch){
        if(stack.isEmpty())return;var count=counts.computeIfAbsent(new ItemType(stack.copy(),true,false),key->new int[3]);
        count[0]+=stack.getCount();if(missing)count[1]+=stack.getCount();if(mismatch)count[2]+=stack.getCount();
    }
    private static Map<String,Coating> coatings(CompoundTag data){
        Map<String,Coating> parts=new LinkedHashMap<>();if(data==null)return parts;
        var registry=Minecraft.getInstance().level.registryAccess();
        if(data.contains("material_data")){var multi=data.getCompound("material_data");for(String name:multi.getAllKeys()){var part=multi.getCompound(name);parts.put(name,new Coating(ItemStack.parseOptional(registry,part.getCompound("consumedItem")),part.getCompound("material")));}}
        else parts.put("material",new Coating(ItemStack.parseOptional(registry,data.getCompound("Item")),data.getCompound("Material")));
        return parts;
    }
    public void count(BlockState state,CompoundTag data,BlockState actual,CompoundTag actualData,boolean ignoreState){
        boolean wrong=actual==null||actual.isAir()||state!=actual&&(!ignoreState||state.getBlock()!=actual.getBlock());
        boolean mismatch=actual!=null&&!actual.isAir()&&wrong;
        var body=ItemRequirement.of(state,null);
        if(!body.isInvalid())for(var item:body.getRequiredItems())if(item.usage==ItemRequirement.ItemUseType.CONSUME)add(item.stack,wrong,mismatch);
        var installed=wrong?Map.<String,Coating>of():coatings(actualData);
        for(var entry:coatings(data).entrySet()){
            var desired=entry.getValue();var current=installed.get(entry.getKey());
            boolean absent=wrong||current==null||current.item.isEmpty();
            boolean different=!absent&&(!desired.material.equals(current.material)||!ItemStack.isSameItemSameComponents(desired.item,current.item));
            add(desired.item,absent||different,mismatch||different);
        }
    }
    public List<MaterialListEntry> merge(List<MaterialListEntry> original){
        var all=new LinkedHashMap<ItemType,int[]>();for(var entry:original){var value=all.computeIfAbsent(new ItemType(entry.getStack(),true,false),key->new int[3]);value[0]+=entry.getCountTotal();value[1]+=entry.getCountMissing();value[2]+=entry.getCountMismatched();}
        counts.forEach((key,value)->{var sum=all.computeIfAbsent(key,k->new int[3]);for(int i=0;i<3;i++)sum[i]+=value[i];});
        var available=MaterialListUtils.getInventoryItemCounts(Minecraft.getInstance().player.getInventory());List<MaterialListEntry> result=new ArrayList<>();
        all.forEach((key,value)->result.add(new MaterialListEntry(key.getStack().copy(),value[0],value[1],value[2],available.getInt(key))));return result;
    }
    public static CompoundTag data(BlockEntity entity){return entity==null?null:entity.saveWithFullMetadata(Minecraft.getInstance().level.registryAccess());}
    public static List<MaterialListEntry> file(LitematicaSchematic schematic,Collection<String> regions){
        var count=new ForgematicaMaterials();var other=new Object2IntOpenHashMap<BlockState>();
        for(String name:regions){var blocks=schematic.getSubRegionContainer(name);if(blocks==null)continue;var size=blocks.getSize();var entities=schematic.getBlockEntityMapForRegion(name);
            for(int y=0;y<size.getY();y++)for(int z=0;z<size.getZ();z++)for(int x=0;x<size.getX();x++){var state=blocks.get(x,y,z);
                if(state.getBlock() instanceof ICopycatBlock)count.count(state,entities==null?null:entities.get(new BlockPos(x,y,z)),null,null,false);else other.addTo(state,1);
            }
        }
        return count.merge(MaterialListUtils.getMaterialList(other,other,new Object2IntOpenHashMap<>(),Minecraft.getInstance().player));
    }
}
