package kr.toxicity.model.api.scheduler;

/**
 * A scheduled task of BetterModel
 */
public interface ModelTask {

    /**
     * Checks this task is canceled
     * @return whether to cancel
     */
    boolean isCancelled();

    /**
     * Cancels this task
     */
    void cancel();
}
