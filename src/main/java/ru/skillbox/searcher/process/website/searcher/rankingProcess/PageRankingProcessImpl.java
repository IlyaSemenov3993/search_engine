package ru.skillbox.searcher.process.website.searcher.rankingProcess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.model.repository.LemmaRepository;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;

import java.util.Collection;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageRankingProcessImpl implements PageRankingProcessor{

    @Autowired
    private LemmaRepository lemmaRepository;

    @Override
    public Collection<RankingPageDTO> process(Collection<PageDTO> pageDTOCollection, Collection<String> lemmas) {

        Collection<PageHelper> pageHelperCollection = pageDTOCollection.stream()
                .map(pageDTO -> new PageHelper(pageDTO, lemmas))
                .collect(Collectors.toList());

        float maxRankSum = this.getMaxRankSum(pageHelperCollection);

        return this.getPageCollection(pageHelperCollection , maxRankSum);
    }


    private float getMaxRankSum(Collection<PageHelper> pageHelperCollection) {
        return (float) pageHelperCollection.stream()
                .mapToDouble(PageHelper::getRankSum)
                .max().getAsDouble();
    }

    private Collection<RankingPageDTO> getPageCollection(Collection<PageHelper> pageHelperCollection, float maxRankSum) {
        Function<PageHelper, RankingPageDTO> pageDTOMapper = pageHelper ->
                new RankingPageDTO(pageHelper.pageDTO , pageHelper.rankSum / maxRankSum);

        return pageHelperCollection.stream()
                .map(pageDTOMapper)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>()));
    }


    public class PageHelper {
        private final PageDTO pageDTO;
        private final Collection<String> lemmaCollection;
        private final float rankSum;

        public PageHelper(PageDTO pageDTO, Collection<String> lemmaCollection) {
            this.pageDTO = pageDTO;
            this.lemmaCollection = lemmaCollection;
            this.rankSum = this.processRankSum();
        }

        private float processRankSum() {
            return lemmaRepository.sumRankByPageIdAndValueIn(pageDTO.getId()
                    , lemmaCollection);
        }

        public float getRankSum() {
            return rankSum;
        }
    }

}
