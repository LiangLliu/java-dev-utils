package com.LiangLliu.utils.file.image;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilsTest {


    URL resource = getClass().getClassLoader().getResource("images/");


    @Test
    public void should_compress_image() throws IOException {
        ImageUtils.compressPic(resource.getPath() + "/1.jpeg", resource.getPath() + "/1_1.jpeg");
    }
}