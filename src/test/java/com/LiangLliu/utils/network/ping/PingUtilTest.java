package com.LiangLliu.utils.network.ping;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class PingUtilTest {

    @Test
    public void should_ping_when_given_ip() throws IOException {
        PingUtil.ping("127.0.0.1", 4);
    }

}
