package com.LiangLliu.utils.network.ping;

import java.io.IOException;
import java.net.InetAddress;

public class PingUtil {

    public static void ping(String ipOrDomain, int count) throws IOException {
        InetAddress remoteHost = InetAddress.getByName(ipOrDomain);

        for (int i = 0; i < count; i++) {
            long startTime = System.currentTimeMillis();

            boolean reachable = remoteHost.isReachable(5000);
            long endTime = System.currentTimeMillis();

            long totalTime = endTime - startTime;

            if (reachable) {
                System.out.println(ipOrDomain + " is reachable: icmp_seq=" + i + " time=" + totalTime + "ms");
            } else {
                System.out.println(ipOrDomain + "is NOT reachable");
            }
        }
    }

}
