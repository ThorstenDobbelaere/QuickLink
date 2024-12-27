package framework.request.listener;

import framework.context.QuickLinkContext;
import framework.context.config.ListenerConfiguration;
import framework.context.config.LogFormatter;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.setup.CallResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SimpleHttpListener implements InputListener{
    private final ListenerConfiguration config;
    private boolean listening = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpListener.class);
    private final LogFormatter logFormatter;

    public SimpleHttpListener(QuickLinkContext context) {
        this.config = context.getListenerConfiguration();
        logFormatter = context.getLogFormatter();
    }

    public void startListening() throws IOException {
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

    private void processHttp(String httpRequest, Socket socket) {
        LOGGER.trace("HTTP request: {}", httpRequest);
        if(httpRequest.isEmpty()){
            return;
        }
        try{
            String firstLine = httpRequest.split("\n")[0];
            String url = firstLine.split(" ")[1];
            processUrl(url, socket);
        } catch (Exception e){
            LOGGER.error("Error processing HTTP request {}", httpRequest, e);
        }

    }

    private void processUrl(String url, Socket socket) throws IOException {
        LOGGER.debug("Received call to {}", url);
        if(url.equals(config.getShutdownUrl())){
            LOGGER.info("Stopping...");
            listening = false;
            HttpResponse ok = new HttpResponse(HttpStatus.OK);
            sendResponse(ok, socket);
            return;
        }

        if(url.equals("/favicon.ico")){
            HttpResponse notFound = new HttpResponse(HttpStatus.NOT_FOUND);
            sendResponse(notFound, socket);
            return;
        }

        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
        HttpResponse response = CallResolver.handleCall(decodedUrl);
        sendResponse(response, socket);
    }

    private void sendResponse(HttpResponse response, Socket socket) throws IOException {
        LOGGER.debug("Response:\n{}", logFormatter.highlight(convertToHttp(response)));

        try (socket; var os = new DataOutputStream(socket.getOutputStream())) {
            os.write(convertToHttp(response).getBytes(StandardCharsets.UTF_8));
        }
    }

    private String convertToHttp(HttpResponse response) {
        return String.format("""
        HTTP/1.1 %d
        Content-Type: %s
        Access-Control-Allow-Origin: *
        Content-Length: %d
        
        %s
        """, response.getStatus().getCode(), response.getContentType().getHttpString(), response.getBody().length(), response.getBody());
    }
}
