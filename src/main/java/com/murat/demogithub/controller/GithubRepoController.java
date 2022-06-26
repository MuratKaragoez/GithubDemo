package com.murat.demogithub.controller;

import com.murat.demogithub.converter.GithubRepoConverter;
import com.murat.demogithub.domain.GithubRepo;
import com.murat.demogithub.response.GithubRepoResponse;
import com.murat.demogithub.service.GithubRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static com.murat.demogithub.controller.advice.GithubRepoControllerAdvice.ValidDate;

@RestController
@RequestMapping(value = "/api/v1/github/repos")
@Validated
public class GithubRepoController {

    @Autowired
    private GithubRepoService githubRepoService;
    @Autowired
    private GithubRepoConverter githubRepoConverter;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<GithubRepoResponse>> findReposByStars(
            @RequestParam(defaultValue = "1000") @Min(1) @Max(1000) int limit,
            @RequestParam(required = false) @ValidDate String created,
            @RequestParam(required = false) String language) {
        List<GithubRepo> reposByStars = githubRepoService.getReposByStars(limit, created, language);
        return new ResponseEntity<>(githubRepoConverter.convert(reposByStars), HttpStatus.OK);
    }
}
