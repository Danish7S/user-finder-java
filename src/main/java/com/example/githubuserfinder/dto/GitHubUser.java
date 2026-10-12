package com.example.githubuserfinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubUser(
        String login,
        Long id,
        String name,
        String bio,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("html_url") String htmlUrl,
        String location,
        String company,
        @JsonProperty("public_repos") int publicRepos,
        int followers,
        int following,
        @JsonProperty("created_at") String createdAt,
        String blog
) {}
