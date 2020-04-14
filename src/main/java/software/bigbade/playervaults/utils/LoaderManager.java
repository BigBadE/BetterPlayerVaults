package software.bigbade.playervaults.utils;

import lombok.RequiredArgsConstructor;
import software.bigbade.playervaults.BetterPlayerVaults;
<<<<<<< HEAD
import software.bigbade.playervaults.loading.FlatfileVaultLoader;
=======
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.loading.PersistentVaultLoader;

@RequiredArgsConstructor
public class LoaderManager {
    private final BetterPlayerVaults main;

    public boolean checkName(String name, int version) {
        return name.equals("flatfile") || (version >= 14 && name.equals("persistent"));
    }

    public IVaultLoader getVaultLoader(String loader) {
        if(loader.equals("persistent")) {
            return new PersistentVaultLoader(main);
<<<<<<< HEAD
        } else if(loader.equals("flatfile")) {
            return new FlatfileVaultLoader(main.getDataFolder().getAbsolutePath());
=======
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
        }
        return null;
    }
}
