package com.example.github_tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tracker.model.Contribution;
import com.github.tracker.model.ContributionDetail;
import com.github.tracker.model.Repository;
import com.github.tracker.model.User;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GitHubApiService {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GitHubApiService() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    public String getUsername(String accessToken) throws IOException {
        HttpGet request = new HttpGet("https://api.github.com/user");
        request.addHeader("Authorization", "token " + accessToken);
        request.addHeader("Accept", "application/vnd.github.v3+json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonNode root = objectMapper.readTree(jsonResponse);
            return root.get("login").asText();
        }
    }

    public String configureWebhook(String repoUrl, String accessToken) {
        try {
            String webhookSecret = UUID.randomUUID().toString();
            String apiUrl = repoUrl.replace("github.com", "api.github.com/repos") + "/hooks";

            HttpPost request = new HttpPost(apiUrl);
            request.addHeader("Authorization", "token " + accessToken);
            request.addHeader("Accept", "application/vnd.github.v3+json");

            String payload = "{"
                    + "\"name\": \"web\","
                    + "\"active\": true,"
                    + "\"events\": [\"push\", \"pull_request\", \"pull_request_review\"],"
                    + "\"config\": {"
                    + "\"url\": \"https://your-app-url.com/api/webhook\","
                    + "\"content_type\": \"json\","
                    + "\"secret\": \"" + webhookSecret + "\""
                    + "}"
                    + "}";

            request.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return webhookSecret;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Contribution> processWebhookPayload(String payload, Repository repository) {
        List<Contribution> contributions = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = root.has("action") ? root.get("action").asText() : "push";

            if (payload.contains("\"commits\"")) {
                // Process push event
                JsonNode commits = root.get("commits");
                JsonNode sender = root.get("sender");

                for (JsonNode commit : commits) {
                    Contribution contribution = new Contribution();
                    contribution.setContributionType(Contribution.ContributionType.COMMIT);
                    contribution.setRepository(repository);

                    // This would be filled in a real implementation
                    // contribution.setUser(findOrCreateUser(sender));

                    contribution.setTimestamp(LocalDateTime.parse(
                            commit.get("timestamp").asText(),
                            DateTimeFormatter.ISO_DATE_TIME));
                    contribution.setDescription(commit.get("message").asText());
                    contribution.setUrl(commit.get("url").asText());

                    // Process file changes
                    int added = 0;
                    int removed = 0;
                    int filesChanged = 0;

                    if (commit.has("added")) {
                        filesChanged += commit.get("added").size();
                        for (JsonNode file : commit.get("added")) {
                            ContributionDetail detail = new ContributionDetail();
                            detail.setContribution(contribution);
                            detail.setFilePath(file.asText());
                            detail.setLinesAdded(1); // GitHub API doesn't provide line counts directly
                            detail.setLinesRemoved(0);
                            added++;

                            contribution.getDetails().add(detail);
                        }
                    }

                    if (commit.has("removed")) {
                        filesChanged += commit.get("removed").size();
                        for (JsonNode file : commit.get("removed")) {
                            ContributionDetail detail = new ContributionDetail();
                            detail.setContribution(contribution);
                            detail.setFilePath(file.asText());
                            detail.setLinesAdded(0);
                            detail.setLinesRemoved(1); // GitHub API doesn't provide line counts directly
                            removed++;

                            contribution.getDetails().add(detail);
                        }
                    }

                    if (commit.has("modified")) {
                        filesChanged += commit.get("modified").size();
                        for (JsonNode file : commit.get("modified")) {
                            ContributionDetail detail = new ContributionDetail();
                            detail.setContribution(contribution);
                            detail.setFilePath(file.asText());
                            detail.setLinesAdded(1); // GitHub API doesn't provide line counts directly
                            detail.setLinesRemoved(1);
                            added++;
                            removed++;

                            contribution.getDetails().add(detail);
                        }
                    }

                    contribution.setLinesAdded(added);
                    contribution.setLinesRemoved(removed);
                    contribution.setFilesChanged(filesChanged);

                    contributions.add(contribution);
                }
            } else if (event.equals("opened") || event.equals("closed") || event.equals("reopened")) {
                // Process pull request events
                JsonNode pullRequest = root.get("pull_request");
                JsonNode sender = root.get("sender");

                Contribution contribution = new Contribution();
                contribution.setContributionType(Contribution.ContributionType.PULL_REQUEST);
                contribution.setRepository(repository);

                // This would be filled in a real implementation
                // contribution.setUser(findOrCreateUser(sender));

                contribution.setTimestamp(LocalDateTime.parse(
                        pullRequest.get("created_at").asText(),
                        DateTimeFormatter.ISO_DATE_TIME));
                contribution.setDescription(pullRequest.get("title").asText());
                contribution.setUrl(pullRequest.get("html_url").asText());

                // GitHub API provides additions and deletions for PRs
                contribution.setLinesAdded(pullRequest.get("additions").asInt());
                contribution.setLinesRemoved(pullRequest.get("deletions").asInt());
                contribution.setFilesChanged(pullRequest.get("changed_files").asInt());

                contributions.add(contribution);
            } else if (event.equals("submitted")) {
                // Process pull request review events
                JsonNode review = root.get("review");
                JsonNode pullRequest = root.get("pull_request");
                JsonNode sender = root.get("sender");

                Contribution contribution = new Contribution();
                contribution.setContributionType(Contribution.ContributionType.REVIEW);
                contribution.setRepository(repository);

                // This would be filled in a real implementation
                // contribution.setUser(findOrCreateUser(sender));

                contribution.setTimestamp(LocalDateTime.parse(
                        review.get("submitted_at").asText(),
                        DateTimeFormatter.ISO_DATE_TIME));
                contribution.setDescription("Review: " + review.get("state").asText());
                contribution.setUrl(review.get("html_url").asText());

                // Reviews don't have lines added/removed directly
                contribution.setLinesAdded(0);
                contribution.setLinesRemoved(0);
                contribution.setFilesChanged(0);

                contributions.add(contribution);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contributions;
    }

    private User findOrCreateUser(JsonNode sender) {
        // This would need to be implemented to find or create a user based on GitHub data
        User user = new User();
        user.setGithubId(sender.get("id").asText());
        user.setUsername(sender.get("login").asText());
        return user;
    }
}