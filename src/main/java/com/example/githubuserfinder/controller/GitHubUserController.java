package com.example.githubuserfinder.controller;

import com.example.githubuserfinder.dto.GitHubUser;
import com.example.githubuserfinder.service.GitHubUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class GitHubUserController {
    private final GitHubUserService userService;

    public GitHubUserController(GitHubUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public GitHubUser getUser(@PathVariable String username) {
        if (username == null || username.isBlank()
                || !username.matches("[A-Za-z0-9-]{1,39}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Enter a valid GitHub username.");
        }
        return userService.findUser(username);
    }
}
