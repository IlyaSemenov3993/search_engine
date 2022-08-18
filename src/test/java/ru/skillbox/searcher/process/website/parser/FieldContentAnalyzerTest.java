package ru.skillbox.searcher.process.website.parser;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.process.website.parser.contentAnalyser.ContentAnalyser;
import helpers.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class FieldContentAnalyzerTest {

    @Autowired
    private ContentAnalyser contentAnalyser;

    @Test
    public void processTest1() throws Exception{
        String sourcePath = "src/test/resources/skillbox.html";
        String content = FileUtils.getFileContent(sourcePath);

        Map<String , Integer> actual = contentAnalyser.process(content);
        Map<String , Integer> expectedPairs = new HashMap<>();

        expectedPairs.put("курс" , 68);
        expectedPairs.put("платформа" , 67);
        expectedPairs.put("skillbox" , 66);
        expectedPairs.put("персонализация" , 8);
        expectedPairs.put("вебинар" , 32);

        for (String key : expectedPairs.keySet()){
            int actualRank = actual.getOrDefault(key , 0);
            int expectedRank = expectedPairs.get(key);

            Assertions.assertEquals(expectedRank , actualRank);
        }
    }

    @Test
    public void processTest2() throws IOException {
        String sourcePath = "src/test/resources/skillbox.html";
        String content = FileUtils.getFileContentWithLimitLines(sourcePath , 20);

        Map<String , Integer> actual = contentAnalyser.process(content);
        Map<String , Integer> expected = new HashMap<>();

        expected.put("skillbox" , 10);
        expected.put("образовательный" , 10);
        expected.put("платформа" , 10);
        expected.put("онлайн" , 10);
        expected.put("курс" , 20);

        Assertions.assertEquals(expected , actual);
    }

    @Test
    public void processNegativeTest(){
        String emptyInput ="";
        Map<String, Integer> result = contentAnalyser.process(emptyInput);
        Assertions.assertTrue(result.isEmpty());

        Executable executable = () -> contentAnalyser.process(null);;
        assertThrows(NullPointerException.class, executable);
    }

}
