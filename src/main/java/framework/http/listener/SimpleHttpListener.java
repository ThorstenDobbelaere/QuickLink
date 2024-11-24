package framework.http.listener;

import framework.context.QuickLinkContext;
import framework.context.configurable.HttpConfiguration;
import framework.http.internal.HttpResponse;
import framework.resolver.CallResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SimpleHttpListener {
    private final HttpConfiguration config;
    private boolean listening = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpListener.class);

    public SimpleHttpListener(QuickLinkContext context) {
        this.config = context.getHttpConfiguration();
    }

    public void start() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(config.getPort())){
            this.listening = true;
            while(this.listening){
                listenAndProcessRequest(serverSocket);
            }
        }
    }

    private void listenAndProcessRequest(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();

        try(var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            readAndProcessRequest(reader, socket);
        }
    }

    private void readAndProcessRequest(BufferedReader reader, Socket socket) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null && !line.isEmpty()){
            builder.append(line).append('\n');
        }

        LOGGER.trace(builder.toString());
        processHttp(builder.toString(), socket);
    }

    private void processHttp(String httpRequest, Socket socket) throws IOException {
        LOGGER.trace("HTTP request: {}", httpRequest);
        String firstLine = httpRequest.split("\n")[0];
        String url = firstLine.split(" ")[1];
        processUrl(url, socket);
    }

    private void processUrl(String url, Socket socket) throws IOException {
        LOGGER.debug("Received call to {}", url);
        if(url.equals(config.getShutdownUrl())){
            listening = false;
        }
        HttpResponse response = CallResolver.handleCall(url);
        sendResponse(response, socket);
    }

    private void sendResponse(HttpResponse response, Socket socket) throws IOException {
        LOGGER.debug("Response: {}", convertToHttp(response));

        try(var os = new DataOutputStream(socket.getOutputStream())){
            os.write(convertToHttp(response).getBytes(StandardCharsets.UTF_8));
        }
    }

    private String convertToHttp(HttpResponse response) {
        return String.format("""
        HTTP/1.1 %d
        Content-Type: %s
        
        %s
        """, response.getStatus().getCode(), response.getContentType().getHttpString(), response.getBody());
    }
}
