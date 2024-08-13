package com.job.githubrepoapi;

import com.job.githubrepoapi.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/repositories")
public class UserController {
    private final GitHubService gitHubService;

    @GetMapping("/{login}")
    public ResponseEntity<List<Map<String, Object>>> getRepositories(
            @PathVariable String login,
            @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = "application/json") String acceptHeader) {
        System.out.println("Accept Header: " + acceptHeader);
        System.out.println("Is application/json: " + acceptHeader.equalsIgnoreCase("application/json"));
        if (!"application/json".equalsIgnoreCase(acceptHeader)) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        List<Map<String, Object>> repositories = gitHubService.getRepositories(login);
        return new ResponseEntity<>(repositories, HttpStatus.OK);
    }
}
