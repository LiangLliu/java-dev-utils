package com.LiangLliu.utils.security.password;

import org.apache.commons.text.RandomStringGenerator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see <a href="https://www.codenong.com/b-java-generate-secure-password">
 * https://www.codenong.com/b-java-generate-secure-password/
 * </a>
 */
public class CommonsTextRandomPassUtils {

    public static String generateCommonTextPassword() {
        String passwordString = generateRandomSpecialCharacters(2)
                .concat(generateRandomNumbers(2))
                .concat(generateRandomAlphabet(2, true))
                .concat(generateRandomAlphabet(2, false))
                .concat(generateRandomCharacters(2));

        List<Character> pwChars = passwordString.chars()
                .mapToObj(data -> (char) data)
                .collect(Collectors.toList());

        Collections.shuffle(pwChars);

        return pwChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static String generateRandomNumbers(int length) {
        RandomStringGenerator pwdGenerator = getRandomStringGenerator(48, 57);
        return pwdGenerator.generate(length);
    }

    public static String generateRandomAlphabet(int length, boolean lowerCase) {
        int low;
        int hi;
        if (lowerCase) {
            low = 97;
            hi = 122;
        } else {
            low = 65;
            hi = 90;
        }
        RandomStringGenerator pwdGenerator = getRandomStringGenerator(low, hi);
        return pwdGenerator.generate(length);
    }

    public static String generateRandomCharacters(int length) {
        RandomStringGenerator pwdGenerator = getRandomStringGenerator(48, 57);
        return pwdGenerator.generate(length);
    }

    public static String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator pwdGenerator = getRandomStringGenerator(33, 45);
        return pwdGenerator.generate(length);
    }

    private static RandomStringGenerator getRandomStringGenerator(int minimumCodePoint,
                                                                  int maximumCodePoint) {
        return new RandomStringGenerator.Builder()
                .withinRange(minimumCodePoint, maximumCodePoint)
                .build();
    }
}
