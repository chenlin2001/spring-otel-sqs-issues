package com.mimecast.architech.springk8sample.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheClientConfig {
    @Value("${apacheClientExample.maxTotalConnections:20}")
    private int maxTotalConnections;

    @Value("${apacheClientExample.connectionTimeout:1000}")
    private long connectionTimeout;

    @Value("${apacheClientExample.socketTimeout:1000}")
    private long socketTimeout;

    @Value("${apacheClientExample.socketTimeout:500}")
    private long idleConnectionTimeout;

    @Bean
    public CloseableHttpClient apacheHttpClient() {
        Timeout connectTimeoutObject = Timeout.ofMilliseconds(connectionTimeout);
        Timeout socketTimeoutObject = Timeout.ofMilliseconds(socketTimeout);

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(connectTimeoutObject)
                .setSocketTimeout(socketTimeoutObject)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(connectTimeoutObject)
                .setConnectionRequestTimeout(connectTimeoutObject)
                .build();

        // Create and configure the connection manager
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        // Create and configure the HttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofMilliseconds(idleConnectionTimeout))
                .build();
    }
}
