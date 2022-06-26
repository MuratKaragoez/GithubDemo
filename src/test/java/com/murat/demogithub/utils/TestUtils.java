package com.murat.demogithub.utils;

import com.murat.demogithub.domain.GithubRepo;
import com.murat.demogithub.domain.GithubRepoWrapper;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static List<GithubRepo> createRandomRepos(int count) {
        List<GithubRepo> repos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            repos.add(GithubRepo.builder()
                    .name("Name " + count)
                    .description("Description " + count)
                    .language("Language " + count)
                    .stars(count)
                    .build());
        }
        return repos;
    }

    public static GithubRepoWrapper createRandomWrapper(int repoCount){
        List<GithubRepo> repos = createRandomRepos(repoCount);
        GithubRepoWrapper wrapper = new GithubRepoWrapper();
        wrapper.setTotal_count(Integer.MAX_VALUE);
        wrapper.setItems(repos);
        return wrapper;
    }
}
