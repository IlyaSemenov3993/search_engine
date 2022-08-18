package ru.skillbox.searcher.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.skillbox.searcher.model.entity.SiteEntity;
import ru.skillbox.searcher.model.repository.SiteRepository;
import ru.skillbox.searcher.process.website.tools.Links;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Configuration
@PropertySource(value = "classpath:/sites.properties")
@ConfigurationProperties(prefix = "site")
public class SiteConfig {

    @Autowired
    private SiteRepository siteRepository;

    private List<Site> list;

    public int getSiteCount() {
        return this.list.size();
    }

    public List<Site> getList() {
        return list;
    }

    public void setList(List<Site> list) {
        this.list = list;
    }

    @PostConstruct
    private void fillFromDataBase() throws MalformedURLException {
        for (SiteEntity siteEntity : siteRepository.findAll()) {
            this.addSite(siteEntity.getUrl());
        }
    }


    public Site addSite(String link) throws MalformedURLException {
        URL url = Links.prepareUrl(link);
        Site result = this.getSiteByUrlHost(url);

        if (result == null) {
            result = new Site(url);
            list.add(result);
        }

        return result;
    }

    public Site getSiteByPageUrl(URL url) {
        return this.getSiteByUrlHost(url);
    }

    public Site getSiteByStringUrl(String link) throws MalformedURLException {
        URL url = Links.prepareUrl(link);
        return this.getSiteByUrlHost(url);
    }

    private Site getSiteByUrlHost(URL url) {
        String host = url.getHost();
        Predicate<Site> urlPredicate = site -> site.getUrl().getHost().equals(host);
        return this.getSiteByPredicate(urlPredicate);
    }

    private Site getSiteByPredicate(Predicate<Site> predicate) {
        return list.stream()
                .filter(predicate)
                .findAny()
                .orElse(null);
    }

    public static class Site {
        private URL url;
        private String name;


        public Site() {

        }

        public Site(String url, String name) throws MalformedURLException {
            this.url = Links.prepareUrl(url);
            this.name = name;
        }

        public Site(URL url) {
            this.url = url;
            this.name = url.getFile();
        }

        public URL getUrl() {
            return url;
        }

        public String getStringUrl() {
            return url.toString();
        }

        public void setUrl(String url) throws MalformedURLException {
            this.url = Links.prepareUrl(url);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Site site = (Site) o;
            return url.equals(site.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url);
        }
    }
}
