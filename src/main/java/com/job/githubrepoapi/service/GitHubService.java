package com.job.githubrepoapi.service;

import com.job.githubrepoapi.model.Branch;
import com.job.githubrepoapi.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private static final String REPOS_MAPPING = "/repos";
    private static final String BRANCHES_MAPPING = "/branches";
    private final RestTemplate restTemplate;
    @Value("${github.api.repos.url}")
    String githubRepoUrlApi;
    @Value("${github.api.branches.url}")
    String githubBranchUrlApi;

    public List<Map<String, Object>> getRepositories(String login) {
        Repository[] repositories = getRepositoryResponseFromGitHubApi(login);
        return Arrays.stream(repositories).filter(repository -> !repository.isFork()).map(repository -> mapRepositoryToMap(repository, mapBranchesToBranchList(login, repository.getName()))).toList();
    }

    List<Map<String, String>> mapBranchesToBranchList(String login, String repoName) {
        return Arrays.stream(getBranchResponseFromGitHubApi(login, repoName)).map(branch -> Map.of("Branch Name", branch.getName(), "Last Commit SHA", branch.getCommit().getSha())).collect(Collectors.toList());
    }

    Map<String, Object> mapRepositoryToMap(Repository repository, List<Map<String, String>> branchList) {
        return Map.of("Repository Name", repository.getName(), "Owner Login", repository.getOwner().getLogin(), "Branches", branchList);
    }

    private Repository[] getRepositoryResponseFromGitHubApi(String login) {
        ResponseEntity<Repository[]> response = restTemplate.getForEntity(createRepoUrl(login), Repository[].class);
        return response.getBody();
    }

    private Branch[] getBranchResponseFromGitHubApi(String login, String repoName) {
        String branchesUrl = createBranchUrl(login, repoName);
        ResponseEntity<Branch[]> branchesResponse = restTemplate.getForEntity(branchesUrl, Branch[].class);
        return branchesResponse.getBody();
    }

    String createRepoUrl(String login) {
        return String.format("%s%s%s", githubRepoUrlApi, login, REPOS_MAPPING);
    }

    String createBranchUrl(String login, String repoName) {
        return String.format("%s%s/%s%s", githubBranchUrlApi, login, repoName, BRANCHES_MAPPING);
    }
}
