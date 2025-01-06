package com.dongwoo.SQM.config;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {
/*
    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException {
        // SSLContext 설정: 모든 인증서를 신뢰하는 TrustManager 설정
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // SSLContext 객체 생성
        SSLContext sslContext = SSLContext.getInstance("TLS");
        try {
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // HttpClient 설정: 커넥션 풀과 SSLContext 설정
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(50);  // 최대 커넥션 수
        poolingConnManager.setDefaultMaxPerRoute(20);  // 각 호스트 당 최대 커넥션 수

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)  // SSLContext 설정
                .setConnectionManager(poolingConnManager)  // 커넥션 풀 설정
                .build();

        // HttpClient를 RestTemplate에 설정
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(3000);  // 연결 타임아웃 설정

        // RestTemplate 생성
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }*/
}
