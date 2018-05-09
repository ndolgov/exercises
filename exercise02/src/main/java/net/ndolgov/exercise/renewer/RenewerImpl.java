package net.ndolgov.exercise.renewer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Renewer for each update:
 * <ul>
 * <li>notifies the callback</li>
 * <li>calls its storage to save the new value</li>
 * <li>in case of a previously unseen key, sets a new timer for the key</li>
 * </ul>
 */
public final class RenewerImpl<K, V> implements Renewer<K, V> {
    private static Logger log = Logger.getLogger("RenewerImpl");

    private final Storage<K, V> storage;

    private final Timer timer;

    private final RenewCallback<K, V> callback;

    public RenewerImpl(Storage<K, V> storage, Timer timer, RenewCallback<K, V> callback) {
        this.storage = storage;
        this.timer = timer;
        this.callback = callback;
    }

    @Override
    public final void update(K key, V value) {
        checkNotNull(key);

        try {
            callback.onUpdate(key, value);

            final boolean isNewKey = !storage.containsKey(key);
            storage.put(key, value);

            // try to make duplicated timers very unlikely (ABA still a problem)
            final boolean setTimer = isNewKey && (value != null) && value.equals(storage.get(key));
            if (setTimer) {
                timer.set(new RenewCommand(key, value));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Update failed for key: " + key, e);
        }
    }

    private final class RenewCommand implements Runnable {
        private final K key;

        private final V value;

        public RenewCommand(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void run() {
            try {
                callback.onUpdate(key, value);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Callback invocation failed for key: " + key, e);
            }
        }
    }
}
