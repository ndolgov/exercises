package net.ndolgov.fourtytwo.etl;

import java.io.Serializable;
import java.sql.Timestamp;

public class IoTEvent implements Serializable {
    public int device;

    public int metric;

    public long timestamp;

    public double value;

    public IoTEvent() {
    }

    public IoTEvent(int device, int metric, long timestamp, double value) {
        this.device = device;
        this.metric = metric;
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
