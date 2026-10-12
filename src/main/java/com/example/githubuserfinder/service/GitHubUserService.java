package com.example.githubuserfinder.service;

import com.example.githubuserfinder.dto.GitHubUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GitHubUserService {
    private final RestClient restClient;

    public GitHubUserService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    public GitHubUser findUser(String username) {
        try {
            GitHubUser user = restClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .body(GitHubUser.class);

            if (user == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY, "GitHub returned an empty response.");
            }
            return user;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "GitHub username not found.");
        } catch (HttpClientErrorException.Forbidden ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "GitHub API rate limit reached. Please try again later.");
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "GitHub API request failed.");
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to connect to GitHub. Check your internet connection.");
        }
    }
}
