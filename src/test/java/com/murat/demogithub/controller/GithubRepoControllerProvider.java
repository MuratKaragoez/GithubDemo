package com.murat.demogithub.controller;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class GithubRepoControllerProvider {

    private static Stream<Arguments> provideSequenceOfRequestParam() {
        return Stream.of(
                Arguments.of(10, "1992-04-22", "Java"),
                Arguments.of(50, "1990-04-20", "JavaScript"),
                Arguments.of(500, "2010-02-18", "Typescript"),
                Arguments.of(1000, "1992-04-22", ""),
                Arguments.of(500, "2021-08-21", "")
        );
    }
}
