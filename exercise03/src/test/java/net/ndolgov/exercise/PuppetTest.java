package net.ndolgov.exercise;


import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class PuppetTest {

    public static final String SAMPLE_CFG = "target/test-classes/sample.cfg";

    @Test (enabled = false)
    public void test() throws Exception {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(8080)
                .setServerInfo("Test/1.1")
                .setSocketConfig(socketConfig)
                .registerHandler("*", new HttpRequestHandler() {
                    @Override
                    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
                        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
                        HttpConnection conn = coreContext.getConnection(HttpConnection.class);
                        response.setStatusCode(HttpStatus.SC_OK);
                        FileEntity body = new FileEntity(new File(SAMPLE_CFG), ContentType.create("text/html", (Charset) null));
                        response.setEntity(body);
                    }
                })
                .create();
        server.start();

        final Server server2 = new Server("localhost");
        Thread.sleep(10_000);
    }


    @Test
    public void testParser() throws Exception {
        final Parser parser = new Parser(new FileReader(SAMPLE_CFG));
        final Map<String, String> kvs = parser.parse();
        assertEquals(2, kvs.size());
        assertEquals("http://host.com/path/to/hello", kvs.get("/tmp/hello"));
        assertEquals("http://host.com/bar/foo", kvs.get("/tmp/foo/bar"));
    }
}
