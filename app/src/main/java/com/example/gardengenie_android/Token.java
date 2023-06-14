package com.example.gardengenie_android;

public class Token {
    private static Token instance;
    private String token;

    private Token() {
        // 직접적인 인스턴스화를 방지하기 위해 private 생성자 사용
    }

    public static synchronized Token getInstance() {
        if (instance == null) {
            instance = new Token();
        }
        return instance;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}