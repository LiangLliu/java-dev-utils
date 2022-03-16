package com.LiangLliu.utils.file.image;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

public class ImageUtils {

    /**
     * 图片压缩
     */
    public static void compressPic(String srcFilePath, String descFilePath) throws IOException {

        Thumbnails.of(new File(srcFilePath))
                .scale(0.5)
                .toFile(descFilePath);
    }

}
