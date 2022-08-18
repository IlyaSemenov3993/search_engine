package ru.skillbox.searcher.process.lemma;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class LemmatizatorTest {

    @Test
    public void processTextWithCountTest1() {
        String input = "Кто скачет, кто мчится под хладною мглой?\n" +
                "Отец запоздалый, с ним сын молодой.\n" +
                "К отцу, весь издрогнув, малютка приник;\n" +
                "Обняв, его держит и греет старик.";

        Map<String, Integer> expected = new HashMap<>();
        expected.put("скакать", 1);
        expected.put("мчаться", 1);
        expected.put("издрогнуть", 1);
        expected.put("приникнуть", 1);
        expected.put("обнять", 1);
        expected.put("держать", 1);
        expected.put("греть", 1);
        expected.put("хладный", 1);
        expected.put("запоздалый", 1);
        expected.put("молодой", 1);
        expected.put("молодая", 1);
        expected.put("мгла", 1);
        expected.put("сын", 1);
        expected.put("отец", 2);
        expected.put("мгла", 1);
        expected.put("малютка", 1);
        expected.put("старик", 1);
        expected.put("весь", 1);
        expected.put("весить", 1);

        Map<String,Integer> actual = Lemmatizator.processTextWithCount(input);

        assertEquals(expected , actual);
    }

    @Test
    public void processTextWithCountTest2() {
        String input = "Кто скачет, кто cat мчится под хладною мглой?\n" +
                "Отец запоздалый, с ним сын молодой.\n" +
                "К отцу, весь издрогнув, малютка приник;\n" +
                "Обняв, его держит и греет старик cat.";

        Map<String, Integer> expected = new HashMap<>();
        expected.put("скакать", 1);
        expected.put("мчаться", 1);
        expected.put("издрогнуть", 1);
        expected.put("приникнуть", 1);
        expected.put("обнять", 1);
        expected.put("держать", 1);
        expected.put("греть", 1);
        expected.put("хладный", 1);
        expected.put("запоздалый", 1);
        expected.put("молодой", 1);
        expected.put("молодая", 1);
        expected.put("мгла", 1);
        expected.put("сын", 1);
        expected.put("отец", 2);
        expected.put("мгла", 1);
        expected.put("малютка", 1);
        expected.put("старик", 1);
        expected.put("весь", 1);
        expected.put("весить", 1);
        expected.put("cat", 2);

        Map<String,Integer> actual = Lemmatizator.processTextWithCount(input);

        assertEquals(expected , actual);
    }

    @Test
    public void processTextTest1() {
        String input = "Кто скачет, кто мчится под хладною мглой?\n" +
                "Отец запоздалый, с ним сын молодой.\n" +
                "К отцу, весь издрогнув, малютка приник;\n" +
                "Обняв, его держит и греет старик.";

        Set<String> expected = new HashSet<>();
        expected.add("скакать");
        expected.add("мчаться");
        expected.add("издрогнуть");
        expected.add("приникнуть");
        expected.add("обнять");
        expected.add("держать");
        expected.add("греть");
        expected.add("хладный");
        expected.add("запоздалый");
        expected.add("молодой");
        expected.add("молодая");
        expected.add("мгла");
        expected.add("сын");
        expected.add("отец");
        expected.add("мгла");
        expected.add("малютка");
        expected.add("старик");
        expected.add("весь");
        expected.add("весить");

        Collection<String> actual = Lemmatizator.processText(input);

        assertEquals(expected , actual);
    }

    @Test
    public void processTextNegativeTest() {
        String input = "";
        Map<String, Integer> result = Lemmatizator.processTextWithCount(input);
        assert (result.isEmpty());

        input = "!,!85942";
        result = Lemmatizator.processTextWithCount(input);
        assert (result.isEmpty());

        String nullInput = null;
        Executable executable = () -> Lemmatizator.processTextWithCount(nullInput);
        Assertions.assertThrows(NullPointerException.class, executable);
    }
}
