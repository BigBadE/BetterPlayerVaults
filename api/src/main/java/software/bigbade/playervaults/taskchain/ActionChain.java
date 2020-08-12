package software.bigbade.playervaults.taskchain;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainTasks;

public class ActionChain {
    private final TaskChain<Void> taskChain;

    public ActionChain() {
        taskChain = TaskChainFactory.getInstance().createTaskChain();
    }

    public ActionChain(String name) {
        taskChain = TaskChainFactory.getInstance().getSharedTaskChain(name);
    }

    public ActionChain async(EnchantmentTask task) {
        taskChain.async(task);
        return this;
    }

    public void execute() {
        taskChain.execute();
    }

    @FunctionalInterface
    public interface EnchantmentTask extends TaskChainTasks.GenericTask {
        @Override
        void runGeneric();
    }
}
