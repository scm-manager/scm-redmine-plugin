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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpRequest;
import sonia.scm.net.ahc.AdvancedHttpRequestWithBody;
import sonia.scm.net.ahc.AdvancedHttpResponse;
import sonia.scm.redmine.dto.IssueStatus;
import sonia.scm.redmine.dto.RedmineIssue;
import sonia.scm.util.HttpUtil;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedmineRestApiServiceTest {

  private static final String URL = "localhost:3000";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "secret";

  @Mock
  private AdvancedHttpRequest advancedHttpRequest;

  @Mock
  private AdvancedHttpRequestWithBody advancedHttpRequestWithBody;

  @Mock
  private AdvancedHttpResponse advancedHttpResponse;

  @Mock
  private AdvancedHttpClient advancedHttpClient;

  private RedmineRestApiService apiService;

  @Before
  public void before() throws IOException {
    apiService = new RedmineRestApiService(advancedHttpClient, URL, USERNAME, PASSWORD);
    when(advancedHttpRequest.request()).thenReturn(advancedHttpResponse);
    when(advancedHttpRequestWithBody.request()).thenReturn(advancedHttpResponse);
  }

  @Test
  public void shouldRetrieveIssueById() throws IOException {
    when(advancedHttpClient.get(HttpUtil.concatenate(URL, RedmineRestApiService.ISSUES_PATH, "1.json"))).thenReturn(advancedHttpRequest);
    when(advancedHttpResponse.content()).thenReturn("{\"issue\":{\"id\":1,\"project\":{\"id\":1,\"name\":\"default project\"},\"tracker\":{\"id\":1,\"name\":\"default tracker\"},\"status\":{\"id\":2,\"name\":\"done\"},\"priority\":{\"id\":1,\"name\":\"normal\"},\"author\":{\"id\":1,\"name\":\"Redmine Admin\"},\"subject\":\"test issue\",\"description\":\"\",\"start_date\":\"2020-09-30\",\"due_date\":null,\"done_ratio\":0,\"is_private\":false,\"estimated_hours\":null,\"total_estimated_hours\":null,\"created_on\":\"2020-09-30T15:18:57Z\",\"updated_on\":\"2020-10-02T10:24:59Z\",\"closed_on\":\"2020-10-02T10:24:59Z\"}}".getBytes());
    when(advancedHttpResponse.isSuccessful()).thenReturn(true);
    final RedmineIssue issue = apiService.getIssueById(1);
    final IssueStatus issueStatus = issue.getStatus();
    assertThat(issueStatus.getName()).isEqualTo("done");
    assertThat(issueStatus.getId()).isEqualTo(2);
  }

  @Test
  public void shouldRetrieveIssueStatuses() throws IOException {
    when(advancedHttpClient.get(HttpUtil.concatenate(URL, RedmineRestApiService.ISSUE_STATUSES_PATH + ".json"))).thenReturn(advancedHttpRequest);
    when(advancedHttpResponse.content()).thenReturn("{\"issue_statuses\":[{\"id\":1,\"name\":\"new\",\"is_closed\":false},{\"id\":2,\"name\":\"done\",\"is_closed\":true}]}".getBytes());
    when(advancedHttpResponse.isSuccessful()).thenReturn(true);
    final List<IssueStatus> issueStatuses = apiService.getStatuses();
    assertThat(issueStatuses).hasSize(2);
    final IssueStatus issueStatus1 = issueStatuses.get(0);
    assertThat(issueStatus1.getName()).isEqualTo("new");
    assertThat(issueStatus1.getId()).isEqualTo(1);
    final IssueStatus issueStatus2 = issueStatuses.get(1);
    assertThat(issueStatus2.getName()).isEqualTo("done");
    assertThat(issueStatus2.getId()).isEqualTo(2);
  }

  @Test
  public void shouldUpdateIssueById() throws IOException {
    when(advancedHttpClient.put(HttpUtil.concatenate(URL, RedmineRestApiService.ISSUES_PATH, "1.json"))).thenReturn(advancedHttpRequestWithBody);
    when(advancedHttpResponse.isSuccessful()).thenReturn(true);

    final ObjectMapper objectMapper = new ObjectMapper();
    final RedmineIssue redmineIssue = new RedmineIssue((ObjectNode) objectMapper.readTree("{\"id\":1,\"project\":{\"id\":1,\"name\":\"default project\"},\"tracker\":{\"id\":1,\"name\":\"default tracker\"},\"status\":{\"id\":2,\"name\":\"done\"},\"priority\":{\"id\":1,\"name\":\"normal\"},\"author\":{\"id\":1,\"name\":\"Redmine Admin\"},\"subject\":\"test issue\",\"description\":\"\",\"start_date\":\"2020-09-30\",\"due_date\":null,\"done_ratio\":0,\"is_private\":false,\"estimated_hours\":null,\"total_estimated_hours\":null,\"created_on\":\"2020-09-30T15:18:57Z\",\"updated_on\":\"2020-10-02T10:24:59Z\",\"closed_on\":\"2020-10-02T10:24:59Z\"}"));
    final ObjectNode wrappedIssueNode = objectMapper.createObjectNode();
    wrappedIssueNode.set(RedmineRestApiService.ISSUE_WRAPPER_FIELD_NAME, redmineIssue.toJsonNode());

    apiService.updateIssue(redmineIssue);

    verify(advancedHttpRequestWithBody).jsonContent(wrappedIssueNode);
  }

}
