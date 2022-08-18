package helpers;

import java.util.Random;

public class RandomWord {

    public static String generate(int length) {
        Random random = new Random();
        char[] word = new char[length];

        for (int i = 0; i < length; i++) {
            word[i] = (char) ('a' + random.nextInt(26));
        }

        return new String(word);
    }
}
