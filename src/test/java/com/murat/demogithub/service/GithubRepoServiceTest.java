package com.murat.demogithub.service;

import com.murat.demogithub.utils.TestUtils;
import com.murat.demogithub.domain.GithubRepo;
import com.murat.demogithub.service.external.GithubAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class GithubRepoServiceTest {
    private static final int DEFAULT_LIMIT = 100;
    private static final int DEFAULT_API_RESPONSE_COUNT = 100;

    @InjectMocks
    private GithubRepoService githubRepoService;

    @Mock
    private GithubAPI githubAPI;

    @Test
    public void testGetReposWithDefaultLimit(){
        Mockito.when(githubAPI.fetchRepoByStarsForPageNumber(1, null, null))
                .thenReturn(TestUtils.createRandomWrapper(DEFAULT_API_RESPONSE_COUNT));

        List<GithubRepo> reposByStars = githubRepoService.getReposByStars(DEFAULT_LIMIT, null, null);
        assertThat("Service did not return the correct list size", reposByStars.size(), equalTo(DEFAULT_API_RESPONSE_COUNT));
        verify(githubAPI, Mockito.times(1)).fetchRepoByStarsForPageNumber(1, null ,null);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 50, 100, 200, 300, 350, 650, 700, 1000})
    public void testGetReposWithLimits(int limit){
        Mockito.when(githubAPI.fetchRepoByStarsForPageNumber(any(Integer.class), eq(null), eq(null)))
                .thenReturn(TestUtils.createRandomWrapper(DEFAULT_API_RESPONSE_COUNT));

        List<GithubRepo> reposByStars = githubRepoService.getReposByStars(limit, null, null);
        assertThat("Service did not return the correct list size", reposByStars.size(), equalTo(limit));
        int numberOfInvocations = (int) Math.ceil(limit / 100.0);
        verify(githubAPI, Mockito.times(numberOfInvocations)).fetchRepoByStarsForPageNumber(any(Integer.class), eq(null), eq(null));
    }
}
