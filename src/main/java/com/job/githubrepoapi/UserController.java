package com.job.githubrepoapi;

import com.job.githubrepoapi.service.GitHubService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<Map<String, Object>>> getRepositories(@PathVariable String login,
                                                                     @RequestHeader("Accept") String acceptHeader) {
        return new ResponseEntity<>(gitHubService.getRepositories(login), HttpStatus.OK);
    }
}
