package software.bigbade.playervaults.taskchain;

import co.aikar.taskchain.TaskChain;
import lombok.Getter;

public abstract class TaskChainFactory {
  @Getter private static TaskChainFactory instance;

  public abstract <T> TaskChain<T> createTaskChain();

  public abstract <T> TaskChain<T> getSharedTaskChain(String name);

  public static void setInstance(TaskChainFactory instance) {
    if (TaskChainFactory.instance == null) {
      TaskChainFactory.instance = instance;
    }
  }
}
