package com.job.githubrepoapi.service;

import com.job.githubrepoapi.model.Branch;
import com.job.githubrepoapi.model.Commit;
import com.job.githubrepoapi.model.Owner;
import com.job.githubrepoapi.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

public class GithubServiceUnitTest {

    @InjectMocks
    private GitHubService gitHubService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        gitHubService.githubRepoUrlApi = "https://api.github.com/users/";
        gitHubService.githubBranchUrlApi = "https://api.github.com/repos/";
    }

    @Test
    void testCreateRepoUrl() {
        String login = "someUser";
        String expectedUrl = "https://api.github.com/users/someUser/repos";

        String actualUrl = gitHubService.createRepoUrl(login);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testCreateBranchUrl() {
        String login = "someUser";
        String repoName = "someRepo";
        String expectedUrl = "https://api.github.com/repos/someUser/someRepo/branches";

        String actualUrl = gitHubService.createBranchUrl(login, repoName);

        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void testMapRepositoryToMap() {
        Owner owner = new Owner("owner1");
        Repository repository = new Repository("repo1", owner, false);
        List<Map<String, String>> branchList = List.of(
                Map.of("Branch Name", "main", "Last Commit SHA", "sha1")
        );

        Map<String, Object> result = gitHubService.mapRepositoryToMap(repository, branchList);

        assertEquals("repo1", result.get("Repository Name"));
        assertEquals("owner1", result.get("Owner Login"));
        assertEquals(branchList, result.get("Branches"));
    }

    @Test
    void testMapBranchesToBranchList() {
        String login = "someUser";
        String repoName = "someRepo";
        Branch[] branches = {
                new Branch("main", new Commit("sha1")),
                new Branch("develop", new Commit("sha2"))
        };

        when(restTemplate.getForEntity(eq("https://api.github.com/repos/someUser/someRepo/branches"), eq(Branch[].class)))
                .thenReturn(new ResponseEntity<>(branches, HttpStatus.OK));

        List<Map<String, String>> branchList = gitHubService.mapBranchesToBranchList(login, repoName);

        assertEquals(2, branchList.size());
        assertEquals("main", branchList.get(0).get("Branch Name"));
        assertEquals("sha1", branchList.get(0).get("Last Commit SHA"));
        assertEquals("develop", branchList.get(1).get("Branch Name"));
        assertEquals("sha2", branchList.get(1).get("Last Commit SHA"));
    }

    @Test
    void testGetRepositories() {
        String login = "someUser";
        Repository[] repositories = {
                new Repository("repo1", new Owner("owner1"), false),
                new Repository("repo2", new Owner("owner2"), true), // Forked repository
                new Repository("repo3", new Owner("owner3"), false)
        };

        Branch[] branchesRepo1 = {
                new Branch("main", new Commit("sha1"))
        };

        Branch[] branchesRepo3 = {
                new Branch("develop", new Commit("sha3"))
        };

        when(restTemplate.getForEntity(eq("https://api.github.com/users/someUser/repos"), eq(Repository[].class)))
                .thenReturn(new ResponseEntity<>(repositories, HttpStatus.OK));
        when(restTemplate.getForEntity(eq("https://api.github.com/repos/someUser/repo1/branches"), eq(Branch[].class)))
                .thenReturn(new ResponseEntity<>(branchesRepo1, HttpStatus.OK));
        when(restTemplate.getForEntity(eq("https://api.github.com/repos/someUser/repo3/branches"), eq(Branch[].class)))
                .thenReturn(new ResponseEntity<>(branchesRepo3, HttpStatus.OK));

        List<Map<String, Object>> result = gitHubService.getRepositories(login);

        assertEquals(2, result.size());
        assertEquals("repo1", result.getFirst().get("Repository Name"));
        assertEquals("owner1", result.getFirst().get("Owner Login"));
        assertEquals(1, ((List<?>) result.getFirst().get("Branches")).size());

        assertEquals("repo3", result.get(1).get("Repository Name"));
        assertEquals("owner3", result.get(1).get("Owner Login"));
        assertEquals(1, ((List<?>) result.get(1).get("Branches")).size());
    }
}
