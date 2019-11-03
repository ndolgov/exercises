package net.ndolgov.fourtytwo.loadgenerator;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public final class RestfulEventSender implements EventSender {
    private final CloseableHttpClient client;

    private final String url;

    public RestfulEventSender(CloseableHttpClient client, String url) {
        this.client = client;
        this.url = url;
    }

    @Override
    public void sendMessage(int deviceId, int metricId, long timestamp, double value) {
        try {
            final HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(toJsonEvent(deviceId, metricId, timestamp, value)));
            httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

            final CloseableHttpResponse response = client.execute(httpPost);
            EntityUtils.consume(response.getEntity());
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != 200) {
                System.out.println("Failed to post message, status code: " + statusLine.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send event", e);
        }
    }
}
