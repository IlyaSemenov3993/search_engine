package ru.skillbox.searcher.process.lemma.morph.morphology;

import org.apache.lucene.morphology.Heuristic;
import org.apache.lucene.morphology.LuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FilteringMorphology extends LuceneMorphology {
    private final Predicate<String> filter;

    public FilteringMorphology(LanguageEnum languageEnum, Predicate<String> filter) throws IOException {
        super(FilteringMorphology.class.getResourceAsStream(languageEnum.getPathToDict())
                , languageEnum.getDecoderEncoder());
        this.filter = filter;
    }

    public List<String> getNormalForms(String s) {
        ArrayList<String> result = new ArrayList();
        int[] ints = this.decoderEncoder.encodeToArray(this.revertWord(s));
        int ruleId = this.findRuleId(ints);
        boolean notSeenEmptyString = true;
        Heuristic[] var6 = this.rules[this.rulesId[ruleId]];
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            Heuristic h = var6[var8];

            String wordInfo = this.grammarInfo[h.getFormMorphInfo()];
            if (!filter.test(wordInfo)) {
                continue;
            }

            String e = h.transformWord(s).toString();
            if (e.length() > 0) {
                result.add(e);
            } else if (notSeenEmptyString) {
                result.add(s);
                notSeenEmptyString = false;
            }
        }

        return result;
    }


}
