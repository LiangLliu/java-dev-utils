package com.LiangLliu.utils.string.pinyin;

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;

/**
 * @see <a href="https://github.com/houbb/pinyin">https://github.com/houbb/pinyin</a>
 */
public class PinYinUtils {

    public static String convert(String text) {
        return PinyinHelper.toPinyin(text, PinyinStyleEnum.NORMAL);
    }
}
