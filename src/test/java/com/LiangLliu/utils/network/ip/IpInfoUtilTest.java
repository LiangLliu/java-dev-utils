package com.LiangLliu.utils.network.ip;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class IpInfoUtilTest {

    @Test
    public void should_get_idv4() {
        String ipV4 = IpInfoUtil.getIpV4();

        assertNotNull(ipV4);
        System.out.println(ipV4);
    }

    @Test
    public void should_get_ip_info() throws IOException {
        IpInfo ipInfo = IpInfoUtil.getIpInfo();
        assertNotNull(ipInfo);
        System.out.println(ipInfo);
    }
}
