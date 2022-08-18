package ru.skillbox.searcher.view;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.skillbox.searcher.config.SiteConfig;
import ru.skillbox.searcher.dto.PageDTO;
import ru.skillbox.searcher.process.website.searcher.pageResult.PageResultProcessorImpl;
import ru.skillbox.searcher.service.search.dto.RankingPageDTO;
import ru.skillbox.searcher.view.view.HtmlEditor;
import ru.skillbox.searcher.view.view.HtmlEditorImpl;

import java.net.URL;
import java.util.Collection;
import java.util.Objects;

public class Page {

    private HtmlEditor htmlEditor;

    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;


    public Page(SiteConfig.Site site , RankingPageDTO rankingPageDTO, Collection<String> stringCollection) {
        PageDTO pageDTO = rankingPageDTO.getPageDTO();
        Collection<String> lemmaCollection = stringCollection;

        this.htmlEditor = new HtmlEditorImpl(lemmaCollection);
        this.site = site.getStringUrl();
        this.siteName = site.getName();
        this.uri = pageDTO.getUrl();
        this.title = this.processTitle(pageDTO);
        this.snippet = htmlEditor.getSnippet(pageDTO.getContent());
        this.relevance = rankingPageDTO.getRelevance();
    }

    public Page() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public float getRelevance() {
        return relevance;
    }

    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }

    private String processTitle(PageDTO pageDTO) {
        String content = pageDTO.getContent();
        Document doc = Jsoup.parse(content);
        return doc.title();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Float.compare(page.relevance, relevance) == 0 && uri.equals(page.uri) && title.equals(page.title) && snippet.equals(page.snippet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, title, snippet, relevance);
    }
}
