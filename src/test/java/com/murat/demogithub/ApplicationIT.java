package com.murat.demogithub;

import com.murat.demogithub.controller.GithubRepoController;
import com.murat.demogithub.response.GithubRepoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@SpringBootTest
public class ApplicationIT {

    @Autowired
    private GithubRepoController githubRepoController;

    @Autowired
    CacheManager cacheManager;


    @ParameterizedTest
    @ValueSource(ints = {1, 10, 50, 100, 200, 300, 1000, 650, 700, 800, 825, 925, 400, 12, 45, 3, 9, 78 })
    public void testGetRepoForLimit(int limit) {
        ResponseEntity<List<GithubRepoResponse>> reposByStars = githubRepoController.findReposByStars(limit, null, null);
        assertThat("HttpStatus must be ok", reposByStars.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Size of Repos is incorrect", reposByStars.getBody().size(), equalTo(limit));
    }

    @Test
    public void testGetRepoFromCache() {
        ResponseEntity<List<GithubRepoResponse>> reposByStars = githubRepoController.findReposByStars(100, null, null);
        assertThat("HttpStatus must be ok", reposByStars.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Size of Repos is incorrect", reposByStars.getBody().size(), equalTo(100));

        githubRepoController.findReposByStars(5, null, null);
        assertThat("The Github API Response was not in Cache", wasAPIResponseCached(1, null ,null), equalTo(true));
        githubRepoController.findReposByStars(50, null, null);
        assertThat("The Github API Response was not in Cache", wasAPIResponseCached(1, null ,null), equalTo(true));
        githubRepoController.findReposByStars(101, null, null);
        assertThat("The Github API Response was not in Cache", wasAPIResponseCached(2, null ,null), equalTo(true));
        githubRepoController.findReposByStars(150, null, null);
        assertThat("The Github API Response was not in Cache", wasAPIResponseCached(2, null ,null), equalTo(true));
        githubRepoController.findReposByStars(201, null, null);
        assertThat("The Github API Response was not in Cache", wasAPIResponseCached(3, null ,null), equalTo(true));

    }

    private boolean wasAPIResponseCached(int pageNumber, String created, String language) {
        return cacheManager.getCache("fetchRepoByStarsForPageNumber")
                .get(new SimpleKey(pageNumber, created, language)) != null;
    }
}
