# Github Scrapper - Spring Boot Application

The application scraps popular repositories (by stars) in a descending order. The Github API has a rate limit and responds quite slowly. Also, the Github API only returns a maximum of 1000 total results. We are using auto paging to get max results when no limit is specified. However, this takes quite a time (up to 30seconds for full 1000 repositories). For this reason, we make use of a `Cache` that speeds up the successive calls.

---

## How to start

Use either the command line or the IDE to build the jar and then run it.

Use `gradlew build` and `java -jar` to start the application.

## Config

It is recommended to use an access token from github with read permissiosn for repositories. But it's not mandatory.
Put the token in `application.properties` for `github.repo.token`

## API 
### `GET /api/v1/github/repos`

> The default `limit` is `1000`. **It is highly recommended to not use the default value, as the response will be slow on the first iteration.**  
  

`GET /api/v1/github/repos?limit={limit}&created={created}&language={language}`

All Request Parameters are optional and interchangeable. 

* **limit**: Limits the number of responses for the repositories, e.g. limit=10 returns exactly 10 repositories.
* **created**: Filters the repositories from the date they were created.
* **language**: Filters the repositories by a programming language
