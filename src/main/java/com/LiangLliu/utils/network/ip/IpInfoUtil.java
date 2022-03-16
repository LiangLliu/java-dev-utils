package com.LiangLliu.utils.network.ip;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class IpInfoUtil {

    public static IpInfo getIpInfo() throws IOException {

        Gson gson = new Gson();
        String url = "http://ipinfo.io/json";

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        return gson.fromJson(response.body().charStream(), IpInfo.class);
    }

    /**
     * 获取本机外网IPV4
     *
     * @return ip
     */
    public static String getIpV4() {
        final String ipV4Url = "https://v4.ident.me";
        return getIp(ipV4Url);
    }

    private static String getIp(String ipv4Url) {

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest
                .newBuilder().uri(URI.create(ipv4Url))
                .build();
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();
    }
}
