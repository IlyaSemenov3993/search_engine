package ru.skillbox.searcher.view.view;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import ru.skillbox.searcher.process.lemma.morph.MultiMorphology;

import java.util.*;
import java.util.stream.Collectors;

public class HtmlEditorImpl implements HtmlEditor {
    private final Set<String> lemmaSet;
    private final Set<String> markedWords;

    public HtmlEditorImpl(Collection<String> lemmaCollection) {
        this.lemmaSet = new HashSet<>(lemmaCollection);
        this.markedWords = new HashSet<>();
    }

    @Override
    public String getSnippet(String content) {
        Document doc = Jsoup.parse(content);

        doc.body().getAllElements()
                .stream()
                .filter(element -> !element.tagName().equals("b"))
                .forEach(this::editTextInElement);

        return doc.toString();
    }


    private void editTextInElement(Element element) {
        if (element.textNodes().isEmpty()) {
            return;
        }

        Element newElement = new Element(element.tag(), "", element.attributes());

        for (Node node : element.childNodes()) {

            if (node instanceof TextNode) {
                String oldText = ((TextNode) node).text();
                String newText = this.getEditedText(oldText);

                if (!oldText.equals(newText)) {
                    newElement.append(newText);
                    continue;
                }
            }

            newElement.appendChild(node);
        }

        element.replaceWith(newElement);
    }


    private String getEditedText(String text) {
        Collection<String> wordsInText = this.getWords(text);
        String result = text;

        for (String word : wordsInText) {
            result = this.processWord(word, result);
        }

        return result;
    }

    private Collection<String> getWords(String text) {
        String[] words = text.split("[^a-zA-Zа-яА-Я]");

        return Arrays.asList(words)
                .stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String processWord(String word, String text) {
        if (markedWords.contains(word)) {
            return this.markWordBold(word, text);
        }

        if (isTargetWord(word)) {
            markedWords.add(word);
            return this.markWordBold(word, text);
        }

        return text;
    }

    private String markWordBold(String word, String text) {
        return text.replaceAll(word, "<b>" + word + "</b>");
    }

    private boolean isTargetWord(String word) {
        List<String> normalForms = MultiMorphology.getNormalForms(word);

        return normalForms.stream()
                .anyMatch(normalWord -> lemmaSet.contains(normalWord));
    }

}
