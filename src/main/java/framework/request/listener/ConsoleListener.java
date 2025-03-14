package framework.request.listener;

import framework.context.QuickLinkContext;
import framework.context.config.ListenerConfiguration;
import framework.request.response.HttpResponse;
import framework.request.response.HttpStatus;
import framework.setup.CallResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConsoleListener implements InputListener{
    private final Scanner scanner;
    private final ListenerConfiguration config;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleListener.class);
    private boolean listening = false;

    public ConsoleListener(QuickLinkContext config) {
        scanner = new Scanner(System.in);
        this.config = config.getListenerConfiguration();
    }

    @Override
    public void startListening() {
        listening = true;
        while (listening) {
            LOGGER.info("Enter your request: ");
            String line = scanner.nextLine();
            processUrl(line);
        }
    }


    private void processUrl(String url) {
        LOGGER.debug("Received call to {}", url);
        if(url.equals(config.getShutdownUrl())){
            LOGGER.info("Stopping...");
            listening = false;
            HttpResponse ok = new HttpResponse(HttpStatus.OK);
            sendResponse(ok);
            return;
        }
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
        HttpResponse response = CallResolver.handleCall(decodedUrl);
        sendResponse(response);
    }

    private void sendResponse(HttpResponse response) {
        LOGGER.info("Response: {} (status = {}, contentType = {})", response.getBody(), response.getStatus(), response.getContentType());
    }

}
