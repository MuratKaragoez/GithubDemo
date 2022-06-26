package com.murat.demogithub.service.external;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.murat.demogithub.controller.advice.GithubRepoControllerAdvice.*;

public class GithubAPIResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(GithubAPIResponseErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String error = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);
        logger.error("Github API not reachable {}", error);
        if (response.getStatusCode()
                .series() == HttpStatus.Series.SERVER_ERROR) {
            throw new GithubRepoAPINotAvailable();
        } else if (response.getStatusCode()
                .series() == HttpStatus.Series.CLIENT_ERROR) {
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new GithubRepoAPIRateLimitExceeded();
            } else if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                throw new GithubRepoAPIUnprocessableEntity(error);
            } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new GithubRepoAPIUnauthorized(error);
            } else {
                throw new GithubRepoAPINotAvailable();
            }
        }
    }
}
