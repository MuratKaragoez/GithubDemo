package com.murat.demogithub.controller;

import com.murat.demogithub.utils.TestUtils;
import com.murat.demogithub.service.GithubRepoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubRepoController.class)
class GithubRepoControllerTest {

    private static final int DEFAULT_LIMIT = 1000;

    @MockBean
    private GithubRepoService githubRepoService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindReposWithoutRequestParameter() throws Exception {
        Mockito.when(githubRepoService.getReposByStars(DEFAULT_LIMIT, null, null))
                .thenReturn(TestUtils.createRandomRepos(DEFAULT_LIMIT));

        mockMvc.perform(get("/api/v1/github/repos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(DEFAULT_LIMIT)));
    }

    @ParameterizedTest
    @MethodSource("com.murat.demogithub.controller.GithubRepoControllerProvider#provideSequenceOfRequestParam")
    public void testFindReposWithRequestParameter(int limit, String created, String language) throws Exception {
        Mockito.when(githubRepoService.getReposByStars(limit, created, language))
                .thenReturn(TestUtils.createRandomRepos(limit));

        mockMvc.perform(get("/api/v1/github/repos?limit={limit}&created={created}&language={language}", limit, created, language))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(limit)));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100, 200, 1000})
    public void testFindReposWithLimit(int limit) throws Exception {
        Mockito.when(githubRepoService.getReposByStars(limit, null, null))
                .thenReturn(TestUtils.createRandomRepos(limit));

        mockMvc.perform(get("/api/v1/github/repos?limit={limit}", limit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(limit)));
    }

    @ParameterizedTest
    @ValueSource(ints = {-8, 0, 5000})
    public void testFindReposWithOutOfBoundLimit(int limit) throws Exception {

        mockMvc.perform(get("/api/v1/github/repos?limit={limit}", limit))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-01-01", "1990-01-01", "2010-12-12"})
    public void testFindReposWithDate(String date) throws Exception {
        Mockito.when(githubRepoService.getReposByStars(DEFAULT_LIMIT, date, null))
                .thenReturn(TestUtils.createRandomRepos(DEFAULT_LIMIT));

        mockMvc.perform(get("/api/v1/github/repos?created={created}", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(DEFAULT_LIMIT)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"20000-01-01", "22-04-1994", "22.04.1992", "22/04/1992"})
    public void testFindReposWithInvalidDate(String date) throws Exception {
        Mockito.when(githubRepoService.getReposByStars(DEFAULT_LIMIT, date, null))
                .thenReturn(TestUtils.createRandomRepos(DEFAULT_LIMIT));

        mockMvc.perform(get("/api/v1/github/repos?created={created}", date))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Java", "JavaScript", "foobar"})
    public void testFindReposWithLanguage(String language) throws Exception {
        Mockito.when(githubRepoService.getReposByStars(DEFAULT_LIMIT, null, language))
                .thenReturn(TestUtils.createRandomRepos(DEFAULT_LIMIT));

        mockMvc.perform(get("/api/v1/github/repos?language={language}", language))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(DEFAULT_LIMIT)));
    }

}
