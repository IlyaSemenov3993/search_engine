package ru.skillbox.searcher.process.website.parser.contentParser;

import org.jsoup.nodes.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.model.entity.FieldEntity;
import ru.skillbox.searcher.model.repository.FieldRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.skillbox.searcher.dto.FieldDTO;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FieldContentParser implements ContentParser<FieldDTO> {

    private static final List<String> CONTENT_ATTRIBUTES = Arrays.asList("content");

    @Autowired
    FieldRepository fieldRepository;

    private Set<FieldDTO> fieldDTOSet;
    private Set<String> selectorSet;

    @PostConstruct
    private void init(){
        this.defineFieldSet();
        this.defineSelectorSet();
    }


    public Map<FieldDTO, String> parse(String content) {
        return fieldDTOSet.stream()
                .collect(Collectors
                        .toMap(fieldDTO -> fieldDTO,
                                fieldDTO -> parse(content, fieldDTO)));
    }

    public String parse(String content, FieldDTO fieldDTO) {
        StringBuilder builder = new StringBuilder();
        List<Element> elementList = Jsoup.parse(content)
                .select(fieldDTO.getSelector());

        for (Element element : elementList) {
            this.appendElementText(element, builder);
        }

        return builder.toString().trim();
    }


    private void appendElementText(Element element, StringBuilder builder) {
        this.appendTextNode(element, builder);

        for (Element child : element.children()) {
            if (!isRedundant(child)) {
                this.appendElementText(child, builder);
            }
        }
    }

    private void appendTextNode(Element element, StringBuilder builder) {
        for (TextNode textNode : element.textNodes()) {

            String text = textNode.text().trim();

            if (!text.isEmpty()) {
                builder.append(" ");
                builder.append(text);
            }
        }

        this.appendAttributesText(element, builder);
    }

    private void appendAttributesText(Element element, StringBuilder builder) {
        for (String attribute : CONTENT_ATTRIBUTES) {

            String text = element.attr(attribute).trim();

            if (!text.isEmpty()) {
                builder.append(" ");
                builder.append(text);
            }
        }
    }

    private boolean isRedundant(Element element) {
        return selectorSet.contains(element.tagName());
    }

    private void defineFieldSet() {
        this.fieldDTOSet = new HashSet<>();

        for (FieldEntity fieldEntity : fieldRepository.findAll()) {
            this.fieldDTOSet.add(new FieldDTO(fieldEntity));
        }
    }

    private void defineSelectorSet() {
        selectorSet = fieldDTOSet.stream()
                .map(FieldDTO::getSelector)
                .collect(Collectors.toSet());
    }

}

