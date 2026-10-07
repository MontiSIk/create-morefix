package dev.kriate.catwalk.mixin;
import dev.kriate.catwalk.OptionalMods;
import java.util.*;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.*;

/** Skip integration patches before Mixin resolves absent addon classes. */
public final class OptionalMixinPlugin implements IMixinConfigPlugin {
    public void onLoad(String pkg){}
    public String getRefMapperConfig(){return null;}
    public boolean shouldApplyMixin(String target,String mixin){
        String name=mixin.substring(mixin.lastIndexOf('.')+1);
        if(name.startsWith("Ladder")&&!OptionalMods.deco())return false;
        if((name.startsWith("Catwalk")||name.startsWith("Railing")||name.startsWith("PipeSupport")||name.equals("StraightPipeSupportTransformMixin"))&&!OptionalMods.deco())return false;
        if((name.contains("Copycat")||name.startsWith("ForgematicaMaterial")||name.equals("ForgematicaBufferMixin"))&&!OptionalMods.copycats())return false;
        if(name.startsWith("Forgematica")&&!OptionalMods.loaded("forgematica"))return false;
        if(target.startsWith("com.copycatsplus.")&&!OptionalMods.copycats())return false;
        if(target.startsWith("com.github.talrey.createdeco.")&&!OptionalMods.deco())return false;
        return true;
    }
    public void acceptTargets(Set<String> own,Set<String> other){}
    public List<String> getMixins(){return null;}
    public void preApply(String target,ClassNode node,String mixin,IMixinInfo info){}
    public void postApply(String target,ClassNode node,String mixin,IMixinInfo info){}
}
