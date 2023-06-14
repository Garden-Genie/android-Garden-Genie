package com.example.gardengenie_android;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientProvider {
    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = createOkHttpClient();
        }
        return client;
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(1200, TimeUnit.SECONDS);
        builder.readTimeout(1200, TimeUnit.SECONDS);
        // 필요한 다른 설정 추가

        return builder.build();
    }
}