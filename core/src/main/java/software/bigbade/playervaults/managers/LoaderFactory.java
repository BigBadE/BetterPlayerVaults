package software.bigbade.playervaults.managers;

import lombok.RequiredArgsConstructor;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.loading.FlatfileVaultLoader;
import software.bigbade.playervaults.loading.PersistentVaultLoader;

@RequiredArgsConstructor
public class LoaderFactory {
  private final BetterPlayerVaults main;

  public boolean checkName(String name, int version) {
    return name.equals("flatfile") || name.equals("mysql") ||
        name.equals("mongo") || (version >= 14 && name.equals("persistent"));
  }

  public IVaultLoader getVaultLoader(String loader) {

    switch (loader) {
    case "persistent":
      return new PersistentVaultLoader(main);
    case "flatfile":
      return new FlatfileVaultLoader(main.getDataFolder().getAbsolutePath());
    default:
      throw new IllegalStateException(
          "checkName passed, but loader not found!");
    }
  }
}
