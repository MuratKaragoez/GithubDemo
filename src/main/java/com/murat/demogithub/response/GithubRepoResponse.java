package com.murat.demogithub.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepoResponse {

    private String name;
    private String description;
    private String url;
    private String language;
    private int stars;
}
