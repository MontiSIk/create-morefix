package dev.kriate.catwalk;

/** Safe during mixin loading as well as normal mod construction. */
public final class OptionalMods {
    private OptionalMods() {}
    public static boolean loaded(String id){
        var mods=net.neoforged.fml.loading.LoadingModList.get();
        return mods!=null&&mods.getModFileById(id)!=null;
    }
    public static boolean copycats(){return loaded("copycats");}
    public static boolean deco(){return loaded("createdeco");}
}
