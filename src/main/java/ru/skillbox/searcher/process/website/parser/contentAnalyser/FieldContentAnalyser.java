package ru.skillbox.searcher.process.website.parser.contentAnalyser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.process.lemma.Lemmatizator;
import ru.skillbox.searcher.dto.FieldDTO;
import ru.skillbox.searcher.process.website.parser.contentParser.ContentParser;

import java.util.HashMap;
import java.util.Map;

@Component
public class FieldContentAnalyser implements ContentAnalyser<String, Integer> {

    @Autowired
    private ContentParser<FieldDTO> contentParser;

    @Override
    public Map<String, Integer> process(String content) {
        Map<String, Integer> result = new HashMap<>();
        Map<FieldDTO, String> fieldMap = contentParser.parse(content);

        for (FieldDTO fieldDTO : fieldMap.keySet()) {

            int fieldWeight = (int) (fieldDTO.getWeight() * 10);
            String fieldContent = fieldMap.get(fieldDTO);
            Map<String , Integer> lemmaMap = Lemmatizator.processTextWithCount(fieldContent);

            for (String lemma : lemmaMap.keySet()){
                
                int lemmaCount = lemmaMap.get(lemma);
                int lemmaIndex = lemmaCount * fieldWeight;

                result.merge(lemma , lemmaIndex , Integer::sum);
            }
        }

        return result;
    }


}
