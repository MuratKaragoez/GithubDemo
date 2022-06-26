package com.murat.demogithub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepoWrapper {
    private int total_count;
    private List<GithubRepo> items;
}
