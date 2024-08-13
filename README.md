# GitHub Repository Service

## Overview

This project is a Spring Boot service that interacts with the GitHub API to retrieve information about a user's repositories. The service provides an API to list all non-fork repositories of a given GitHub user, including the branches and the last commit SHA for each branch.

## Features

- Retrieve all non-fork repositories for a given GitHub user.
- For each repository, list all branches and their last commit SHA.
- Integration with the GitHub REST API.
- Custom error handling for non-existent GitHub users.

## Technologies

- Java 21
- Spring Boot 3.3.2
- Spring Web
- Spring Test
- Mockito
- JUnit 5

## Getting Started

### Prerequisites

- Java 21
- Maven 3.3.2

### Clone the Repository

```bash
git clone https://github.com/your-username/github-repo-service.git
cd github-repo-service
```

## Build the Project

```bash
mvn clean install
```

## Running the Service
```bash
mvn spring-boot:run
```
The service will start at 'http://localhost:8080'. <br><br>

Service is avaiable at http://localhost:8080/swagger-ui/index.html/. <br><br>
The following properties can be configured in the application.properties or application.yml file:

github.api.repos.url: Base URL for the GitHub API to retrieve repositories.  <br>
github.api.branches.url: Base URL for the GitHub API to retrieve branches.

## API Endpoints

### GET Repositories
```bash
GET /repositories/{login}
```

### Response:

200 OK: Returns a list of repositories with branches and their last commit SHA. <br>
404 NOT FOUND: If the GitHub user does not exist.<br>
406 NOT ACCEPTABLE: If the header is incorrect.

## Code Implementation
### GitHubService Class
The GitHubService class is responsible for interacting with the GitHub API and processing the data. Below is a high-level overview of the key methods: <br>

- getRepositories(String login): Fetches all non-fork repositories for a given GitHub user and maps them to a structured format. <br><br>
- mapBranchesToBranchList(String login, String repoName): Maps the branches of a repository to a list of branch details. <br><br>
- mapRepositoryToMap(Repository repository, List<Map<String, String>> branchList): Maps repository details along with branch information to a structured format.

## Testing
### Unit Tests
The project includes unit tests for the GitHubService class. These tests verify the correctness of the logic in the service methods, such as filtering out forked repositories and mapping branches correctly.

### Integration Tests
Integration tests ensure that the service correctly integrates with external systems (simulated by MockRestServiceServer). Below is an example of an integration test for the GitHubService class.