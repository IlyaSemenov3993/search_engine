package ru.skillbox.searcher.process.lemma;

import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.skillbox.searcher.process.lemma.morph.MultiMorphology.getNormalForms;

public class MultiMorphologyTest {

    @Test
    public void getNormalFormsTest() {
        String input = "cat";
        List<String> expected = Arrays.asList("cat");
        List<String> actual = getNormalForms(input);
        assertEquals(expected, actual);

        input = "кот";
        expected = Arrays.asList("кот");
        actual = getNormalForms(input);
        assertEquals(expected, actual);

        input = "коты";
        expected = Arrays.asList("кот", "коты");
        actual = getNormalForms(input);
        assertEquals(expected, actual);
    }

    @Test
    public void getNormalFormsNegativeTest() {
        String input = "или";
        List<String> result = getNormalForms(input);
        assertTrue(result.isEmpty());

        input = "and";
        result = getNormalForms(input);
        assertTrue(result.isEmpty());

        String nullInput = null;
        Executable executable = () -> getNormalForms(nullInput);
        assertThrows(NullPointerException.class, executable);
    }
}
