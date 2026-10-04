package dev.kriate.catwalk.mixin;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(targets="net.neoforged.neoforge.client.gui.ModListScreen$InfoPanel",remap=false)
public abstract class ModInfoDisplayMixin {
    @ModifyVariable(method="setInfo",at=@At("HEAD"),argsOnly=true,ordinal=0)
    private List<String> morefix$displayIdentifier(List<String> info){
        if(info.isEmpty()||info.getFirst()==null||!info.getFirst().contains("Create: MoreFix"))return info;
        var displayed=new ArrayList<String>(info.size());
        for(String line:info)displayed.add(line==null?null:line.replace("catwalk_orientation","Minor fixes"));
        return displayed;
    }
}
