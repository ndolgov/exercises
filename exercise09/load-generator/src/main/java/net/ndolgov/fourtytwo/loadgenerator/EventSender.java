package net.ndolgov.fourtytwo.loadgenerator;

public interface EventSender {
    /**
     * Send a given device metric measurement
     */
    void sendMessage(int deviceId, int metricId, long timestamp, double value);

    default String toJsonEvent(int deviceId, int metricId, long timestamp, double value) {
        return "{\"device\" : " +  deviceId + ", \"metric\" :  " +  metricId + ", \"timestamp\" : " +  timestamp + ", \"value\" : " +  value + "}";
    }
}
