/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sonia.scm.redmine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpRequest;
import sonia.scm.net.ahc.AdvancedHttpRequestWithBody;
import sonia.scm.net.ahc.AdvancedHttpResponse;
import sonia.scm.net.ahc.BaseHttpRequest;
import sonia.scm.redmine.dto.IssueStatus;
import sonia.scm.redmine.dto.IssueStatusesResponse;
import sonia.scm.util.HttpUtil;

import java.io.IOException;
import java.util.List;

public class RedmineRestApiService {

  public static final String ISSUES_PATH = "issues";
  public static final String ISSUE_STATUSES_PATH = "issue_statuses";
  public static final String ISSUE_WRAPPER_FIELD_NAME = "issue";

  private final String url;
  private final String username;
  private final String password;
  private final AdvancedHttpClient httpClient;

  public RedmineRestApiService(AdvancedHttpClient httpClient, String url, String username, String password) {
    this.httpClient = httpClient;
    this.url = url;
    this.username = username;
    this.password = password;
  }

  public ObjectNode getIssueById(Integer issueId) throws IOException {
    // Construct Request
    final AdvancedHttpRequest getIssueRequest = createGetRequest(HttpUtil.concatenate(ISSUES_PATH, issueId.toString()));
    setRequestAuth(getIssueRequest);
    // Send request and handle response
    final AdvancedHttpResponse getIssueResponse = getIssueRequest.request();
    if (!getIssueResponse.isSuccessful()) {
      throw new RuntimeException("Failed to retrieve issue");
    }
    // Map response object
    final ObjectMapper objectMapper = new ObjectMapper();
    return unwrapIssue(objectMapper.readTree(getIssueResponse.content()));
  }

  public void updateIssue(Integer issueId, JsonNode issue) throws IOException {
    // Construct Request
    final AdvancedHttpRequestWithBody putRequest = createPutRequest(
      HttpUtil.concatenate(ISSUES_PATH, issueId.toString()),
      wrapIssue(issue)
    );

    // Send request and handle response
    final AdvancedHttpResponse putIssueResponse = putRequest.request();
    if (!putIssueResponse.isSuccessful()) {
      throw new RuntimeException("Failed to update issue");
    }
  }

  public List<IssueStatus> getStatuses() throws IOException {
    // Construct Request
    final AdvancedHttpRequest getIssueStatusesRequest = createGetRequest(ISSUE_STATUSES_PATH);
    // Send request and handle response
    final AdvancedHttpResponse getIssueStatusesResponse = getIssueStatusesRequest.request();
    if (!getIssueStatusesResponse.isSuccessful()) {
      throw new RuntimeException("Failed to retrieve statuses");
    }
    // Map response object
    final ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(getIssueStatusesResponse.content(), IssueStatusesResponse.class).getIssueStatuses();
  }

  private AdvancedHttpRequest createGetRequest(String relativePath) {
    final AdvancedHttpRequest request = this.httpClient.get(createRequestUrl(relativePath));
    setRequestAuth(request);
    return request;
  }

  private AdvancedHttpRequestWithBody createPutRequest(String relativePath, JsonNode payload) {
    final AdvancedHttpRequestWithBody request = this.httpClient.put(createRequestUrl(relativePath));
    request.jsonContent(payload);
    setRequestAuth(request);
    return request;
  }

  private String createRequestUrl(String relativePath) {
    return HttpUtil.append(url, relativePath + ".json");
  }

  private void setRequestAuth(BaseHttpRequest<?> advancedHttpRequest) {
    advancedHttpRequest.basicAuth(username, password);
  }

  private ObjectNode unwrapIssue(JsonNode obj) {
    return (ObjectNode) obj.get(RedmineRestApiService.ISSUE_WRAPPER_FIELD_NAME);
  }

  private ObjectNode wrapIssue(JsonNode value) {
    final ObjectMapper objectMapper = new ObjectMapper();
    final ObjectNode wrapper = objectMapper.createObjectNode();
    wrapper.set(RedmineRestApiService.ISSUE_WRAPPER_FIELD_NAME, value);
    return wrapper;
  }
}
