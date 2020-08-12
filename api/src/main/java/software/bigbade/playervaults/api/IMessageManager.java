package software.bigbade.playervaults.api;

import lombok.Setter;

public abstract class IMessageManager {
  @Setter private static IMessageManager instance;

  public static IMessageManager getInstance() { return instance; }

  public abstract String getLanguage();

  public abstract String getMessage(String key);
}
