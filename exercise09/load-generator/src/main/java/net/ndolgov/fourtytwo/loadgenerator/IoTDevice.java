package net.ndolgov.fourtytwo.loadgenerator;

import java.util.Random;

public final class IoTDevice implements Runnable {
    private final int deviceId;

    private final int[] metricIds;

    private final EventSender sender;

    private final Random random = new Random();

    public IoTDevice(int deviceId, int[] metricIds, EventSender sender) {
        this.deviceId = deviceId;
        this.metricIds = metricIds;
        this.sender = sender;
    }

    @Override
    public void run() {
        try {
            final long now = System.currentTimeMillis();

            for (final Integer metricId : metricIds) {
                sender.sendMessage(deviceId, metricId, now, random.nextDouble());
            }
        } catch (Exception e) {
            throw new RuntimeException("Device failed: " + deviceId, e); // kill the job
        }
    }
}
