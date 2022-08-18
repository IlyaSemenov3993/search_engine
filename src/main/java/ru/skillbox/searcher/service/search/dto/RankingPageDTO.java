package ru.skillbox.searcher.service.search.dto;

import ru.skillbox.searcher.dto.PageDTO;

public class RankingPageDTO implements Comparable<RankingPageDTO>{
    private PageDTO pageDTO;
    private float relevance;

    public RankingPageDTO(PageDTO pageDTO, float relevance) {
        this.pageDTO = pageDTO;
        this.relevance = (int)(relevance * 100) / 100.f;
    }

    public PageDTO getPageDTO() {
        return pageDTO;
    }

    public float getRelevance() {
        return relevance;
    }


    @Override
    public int compareTo(RankingPageDTO o) {
        return (int) Math.signum(o.relevance - this.relevance );
    }
}
