package net.ndolgov.exercise.renewer;

public interface Timer {
    /**
     * Set a periodic timer with the preconfigured timeout for a given command
     * @param command the command to execute when the timeout expires
     */
    void set(Runnable command);
}
