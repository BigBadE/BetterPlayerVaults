package software.bigbade.playervaults.utils;

import lombok.RequiredArgsConstructor;
import software.bigbade.playervaults.BetterPlayerVaults;
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
        }
        return null;
    }
}
