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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link RedmineIssueMatcher}.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class RedmineIssueMatcherTest {

    @InjectMocks
    private RedmineIssueMatcher issueMatcher;

    /**
     * Tests {@link RedmineIssueMatcher}.
     */
    @Test
    public void testIssueMatching() {
        assertIssueMatches("#42", "fixed bug #42, by adding some stuff");
        assertIssueMatches("#422", "#422 should now be fixed");
        assertIssueMatches("#4222", "fixed #4222");
        assertIssueMatches("#4222", "fixed #4222");
        assertIssueMatches("#42", "old style issue id (#42)");
    }

    private void assertIssueMatches(String expectedKey, String message){
        Pattern pattern = issueMatcher.getKeyPattern();
        Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find());
        assertEquals(expectedKey, issueMatcher.getKey(matcher));
    }

}
