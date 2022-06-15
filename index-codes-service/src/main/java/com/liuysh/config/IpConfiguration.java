package com.liuysh.config;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Liuysh
 * @date 2022/6/15 15:31
 * @Description:
 */
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent> {
    private int serverPort;
    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.serverPort = webServerInitializedEvent.getWebServer().getPort();
    }
    public int getServerPort() {
        return serverPort;
    }
}
