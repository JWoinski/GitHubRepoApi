package com.job.githubrepoapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
public class GitHubServiceIntegrationTest {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetRepositories() {
        String login = "someUser";

        String repositoriesJson = """
                [
                    {
                        "name": "repo1",
                        "owner": {"login": "owner1"},
                        "fork": false
                    },
                    {
                        "name": "repo2",
                        "owner": {"login": "owner2"},
                        "fork": true
                    },
                    {
                        "name": "repo3",
                        "owner": {"login": "owner3"},
                        "fork": false
                    }
                ]
                """;

        mockServer.expect(requestTo("https://api.github.com/users/someUser/repos"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(repositoriesJson, MediaType.APPLICATION_JSON));

        String branchesRepo1Json = """
                [
                    {
                        "name": "main",
                        "commit": {"sha": "sha1"}
                    }
                ]
                """;

        String branchesRepo3Json = """
                [
                    {
                        "name": "develop",
                        "commit": {"sha": "sha3"}
                    }
                ]
                """;

        mockServer.expect(requestTo("https://api.github.com/repos/someUser/repo1/branches"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(branchesRepo1Json, MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo("https://api.github.com/repos/someUser/repo3/branches"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(branchesRepo3Json, MediaType.APPLICATION_JSON));

        List<Map<String, Object>> result = gitHubService.getRepositories(login);

        assertEquals(2, result.size());

        Map<String, Object> repo1 = result.getFirst();
        assertEquals("repo1", repo1.get("Repository Name"));
        assertEquals("owner1", repo1.get("Owner Login"));
        assertEquals(1, ((List<?>) repo1.get("Branches")).size());

        Map<String, Object> repo3 = result.get(1);
        assertEquals("repo3", repo3.get("Repository Name"));
        assertEquals("owner3", repo3.get("Owner Login"));
        assertEquals(1, ((List<?>) repo3.get("Branches")).size());

        mockServer.verify();
    }
}
