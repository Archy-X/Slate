package dev.aurelium.slate.scheduler;

public class WrappedTask {
    private Object originalTask;

    public WrappedTask(Object originalTask) {
        this.originalTask = originalTask;
    }

    public void cancel() {
        // Call cancel method using reflection
        try {
            originalTask.getClass().getMethod("cancel").invoke(originalTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
