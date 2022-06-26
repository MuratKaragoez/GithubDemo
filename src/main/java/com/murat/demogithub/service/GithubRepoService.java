package com.murat.demogithub.service;

import com.murat.demogithub.domain.GithubRepo;
import com.murat.demogithub.service.external.GithubAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.murat.demogithub.service.external.GithubAPI.GITHUB_API_MAX_PAGE_RESPONSE;

@Service
public class GithubRepoService {

    @Autowired
    private GithubAPI githubAPI;


    public List<GithubRepo> getReposByStars(int limit, String created, String language) {
        List<GithubRepo> responses = new ArrayList<>();
        if (limit <= GITHUB_API_MAX_PAGE_RESPONSE) {
            responses.addAll((githubAPI.fetchRepoByStarsForPageNumber(1, created, language).getItems()));
        } else {
            int pageNumber = 1;
            int counter = limit;
            while (counter > 0) {
                responses.addAll(githubAPI.fetchRepoByStarsForPageNumber(pageNumber, created, language).getItems());
                pageNumber++;
                counter -= 100;
            }
        }
        return responses.subList(0, limit);
    }
}
