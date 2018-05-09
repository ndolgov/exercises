package net.ndolgov.exercise;

import java.io.File;
import java.util.Map;

public class Handler {
    public static void handle(Map<String, String> kvs) {
        kvs.forEach((localPath, url) -> {
            try {
                if (!new File(localPath).exists()) {
                    Poller.download(url, localPath);
                }
            } catch (Exception e) {
                System.out.println("Failed to synch: " + localPath);
            }
        });
    }
}
