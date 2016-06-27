/***
 * Copyright (c) 2015, Sebastian Sdorra
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * https://bitbucket.org/sdorra/scm-manager
 * 
 */

package sonia.scm.redmine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sonia.scm.redmine.config.RedmineConfiguration;

/**
 * Unit tests for {@link RedmineIssueMatcher}.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class RedmineIssueMatcherTest {

    @Mock
    private RedmineConfiguration configuration;
    
    @InjectMocks
    private RedmineIssueMatcher issueMatcher;
    
    /**
     * Setup mocks and test class.
     */
    @Before
    public void setUp(){
        when(configuration.getUrl()).thenReturn("http://redmine.org");
    }
   
    /**
     * Tests {@link RedmineIssueMatcher}.
     */
    @Test
    public void testIssueMatching() {
        assertIssueMatches("42", "fixed bug #42, by adding some stuff");
        assertIssueMatches("422", "#422 should now be fixed");
        assertIssueMatches("4222", "fixed #4222");
        assertIssueMatches("4222", "fixed #4222");
        assertIssueMatches("42", "old style issue id (#42)");
    }
    
    private void assertIssueMatches(String expectedKey, String message){
        Pattern pattern = issueMatcher.getKeyPattern();
        Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find());
        assertEquals(expectedKey, issueMatcher.getKey(matcher));
        assertEquals(
            "<a target=\"_blank\" href=\"http://redmine.org/issues/$1\">$0</a>", 
            issueMatcher.getReplacement(matcher)
        );
    }

}