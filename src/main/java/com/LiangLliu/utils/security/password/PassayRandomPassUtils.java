package com.LiangLliu.utils.security.password;

import org.passay.*;

import java.util.List;

import static org.passay.DictionarySubstringRule.ERROR_CODE;

/**
 * @see <a href="https://www.codenong.com/b-java-generate-secure-password">
 * https://www.codenong.com/b-java-generate-secure-password/
 * </a>
 *
 * <a href="https://blog.csdn.net/neweastsun/article/details/109430846"></a>
 */
public class PassayRandomPassUtils {

    private static final int DEFAULT_LENGTH = 2;

    public static String generatePassword(int length) {

        PasswordGenerator gen = new PasswordGenerator();
        return gen.generatePassword(length, getCharacterRules());
    }

    private static List<CharacterRule> getCharacterRules() {

        CharacterRule lowerCaseRule = getLowerCaseChars(DEFAULT_LENGTH);
        CharacterRule upperCaseRule = getUpperCaseChars(DEFAULT_LENGTH);
        CharacterRule digitRule = getDigitChars(DEFAULT_LENGTH);


        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "[emailprotected]#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return List.of(lowerCaseRule, upperCaseRule, digitRule, splCharRule);
    }

    private static CharacterRule getUpperCaseChars(int length) {
        return new CharacterRule(EnglishCharacterData.UpperCase, length);
    }

    private static CharacterRule getLowerCaseChars(int length) {
        return new CharacterRule(EnglishCharacterData.LowerCase, length);
    }

    private static CharacterRule getDigitChars(int length) {
        return new CharacterRule(EnglishCharacterData.Digit, length);
    }
}
