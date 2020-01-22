package com.onestore.sample.config;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import javax.annotation.PreDestroy;

/**
 * Created by a1000074 on 06/11/2019.
 */
@Configuration
public class ApplicationConfig implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${tomcat.ajp.protocol}")
    String ajpProtocol;
    @Value("${tomcat.ajp.port}")
    int ajpPort;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    // application call this method after shutdown
    @PreDestroy
    public void onShutdown() {
        //TODO
    }

    // application call this method on starting time
    @Override
    public void run(String... strings) throws Exception {
        log.info("Initialize custom settings ..") ;
    }

    // application call this method before shutdown
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.info("Pre-processing before shutdown ..");
//        this.registry.stop();

        //TODO logic before shut-down
    }

    // Network Setting
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createAjpConnector());
        return tomcat;
    }

    private Connector createAjpConnector() {
        Connector ajpConnector = new Connector(ajpProtocol);
        ajpConnector.setPort(ajpPort);
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("http");
        return ajpConnector;
    }
}
