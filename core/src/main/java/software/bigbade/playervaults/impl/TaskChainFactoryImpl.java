package software.bigbade.playervaults.impl;

import co.aikar.taskchain.TaskChain;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.taskchain.TaskChainFactory;

public class TaskChainFactoryImpl extends TaskChainFactory {
    private final BetterPlayerVaults main;

    public TaskChainFactoryImpl(BetterPlayerVaults main) {
        TaskChainFactory.setInstance(this);
        this.main = main;
    }

    @Override
    public <T> TaskChain<T> createTaskChain() {
        return main.newChain();
    }

    @Override
    public <T> TaskChain<T> getSharedTaskChain(String name) {
        return main.newSharedChain(name);
    }
}
