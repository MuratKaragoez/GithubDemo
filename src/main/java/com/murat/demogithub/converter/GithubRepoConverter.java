package com.murat.demogithub.converter;

import com.murat.demogithub.domain.GithubRepo;
import com.murat.demogithub.response.GithubRepoResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GithubRepoConverter implements Converter<List<GithubRepo>, List<GithubRepoResponse>> {

    @Override
    public List<GithubRepoResponse> convert(List<GithubRepo> source) {
        if (source == null || source.size() == 0) {
            return Collections.emptyList();
        }
        return source.stream().map(repo ->
                        GithubRepoResponse.builder()
                                .name(repo.getName())
                                .description(repo.getDescription())
                                .language(repo.getLanguage())
                                .url(repo.getHtmlUrl())
                                .stars(repo.getStars()).build())
                .collect(Collectors.toList());
    }
}
