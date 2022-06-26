package com.murat.demogithub.service.external;

import com.murat.demogithub.domain.GithubRepoWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubAPI {
    public static final int GITHUB_API_MAX_PAGES = 10;
    public static final int GITHUB_API_MAX_PAGE_RESPONSE = 100;

    private static final Logger logger = LoggerFactory.getLogger(GithubAPI.class);
    private static final String BASE_REPO_SEARCH_API = "https://api.github.com/search/repositories";
    private static final String REPOS_BY_STARS_AT_PAGE_DESC = BASE_REPO_SEARCH_API + "?sort=stars&order=desc&per_page=100&page={pageNumber}&q=stars:>=2 language:{language}";
    private static final String REPOS_BY_STARS_AND_DATE_AT_PAGE_DESC = BASE_REPO_SEARCH_API + "?sort=stars&order=desc&per_page=100&page={pageNumber}&q=created:>={created} language:{language}";

    @Value("${github.repo.token}")
    private String accessToken;

    private final RestTemplate restTemplate;

    @Autowired
    public GithubAPI(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .errorHandler(new GithubAPIResponseErrorHandler())
                .build();
    }

    @Cacheable(value = "fetchRepoByStarsForPageNumber", cacheManager = "githubRepoCacheManager")
    public GithubRepoWrapper fetchRepoByStarsForPageNumber(int pageNumber, String created, String language) {
        if (created != null) {
            String url = filterQueryParam(REPOS_BY_STARS_AND_DATE_AT_PAGE_DESC, language);
            logger.info("Sending request to {} with params {} {} {}", url, pageNumber, created, language);
            return restTemplate.exchange(url, HttpMethod.GET, createHeaders(), GithubRepoWrapper.class, pageNumber, created, language).getBody();
        } else {
            String url = filterQueryParam(REPOS_BY_STARS_AT_PAGE_DESC, language);
            logger.info("Sending request to {} with params {} {}", url, pageNumber, language);
            return restTemplate.exchange(url, HttpMethod.GET, createHeaders(), GithubRepoWrapper.class, pageNumber, language).getBody();
        }
    }

    private String filterQueryParam(String url, String language) {
        if (language != null) {
            return url;
        }
        return url.replace(" language:{language}", "");
    }

    private HttpEntity<String> createHeaders() {
        if (accessToken == null || accessToken.length() == 0) {
            logger.warn("!!! You did not set an access token. API Access rate is limited.");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token " + accessToken);
        return new HttpEntity<>(null, headers);
    }
}
