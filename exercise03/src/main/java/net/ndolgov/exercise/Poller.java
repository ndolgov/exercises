package net.ndolgov.exercise;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.*;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

final class Poller {
    private final HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
    private final static ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
    private final DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
    private final HttpHost host;
    private final String url;
    private final HttpCoreContext coreContext;
    private final HttpProcessor httpproc;

    public Poller(String url) {
        this.url = url;
        this.host = new HttpHost(url, 8080);

        this.httpproc = HttpProcessorBuilder.create()
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Test/1.1"))
                .add(new RequestExpectContinue(true)).build();

        coreContext = HttpCoreContext.create();
        coreContext.setTargetHost(host);
    }

    private HttpEntity download() {
        try {
            if (!conn.isOpen()) {
                Socket socket = new Socket(host.getHostName(), host.getPort());
                conn.bind(socket);
            }
            BasicHttpRequest request = new BasicHttpRequest("GET", url);

            httpexecutor.preProcess(request, httpproc, coreContext);
            HttpResponse response = httpexecutor.execute(request, conn, coreContext);
            httpexecutor.postProcess(response, httpproc, coreContext);

            return response.getEntity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void download(String url, String destination) {
        final Poller poller = new Poller(url);

        try {
            Files.copy(poller.download().getContent(), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download configuration", e);
        } finally {
            poller.close();
        }
    }

    private void close() {
        try {
            conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
