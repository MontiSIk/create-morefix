package dev.kriate.catwalk.copylink;
import com.copycatsplus.copycats.CCBlocks;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.*;
import net.neoforged.bus.api.IEventBus;
public final class CopyLinks {
    public static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(Registries.BLOCK,"catwalk_orientation");
    public static final DeferredHolder<Block,CoveredLinkBlock> BLOCK=BLOCKS.register("copycat_redstone_link",()->new CoveredLinkBlock(BlockBehaviour.Properties.ofFullCopy(CCBlocks.COPYCAT_BLOCK.get()).dropsLike(AllBlocks.REDSTONE_LINK.get())));
    public static final com.simibubi.create.foundation.data.CreateRegistrate REG=com.simibubi.create.foundation.data.CreateRegistrate.create("catwalk_orientation");
    public static final com.tterrag.registrate.util.entry.BlockEntityEntry<CoveredLinkEntity> ENTITY=REG.blockEntity("copycat_redstone_link",CoveredLinkEntity::new).validBlock(BLOCK::get).register();
    public static void register(IEventBus bus){BLOCKS.register(bus);REG.registerEventListeners(bus);}
}



