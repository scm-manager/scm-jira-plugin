/**
 * *
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
package sonia.scm.jira;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util methods for handling jira issue keys.
 * 
 * @author Sebastian Sdorra <s.sdorra@gmail.com>
 */
public final class IssueKeys {

    private static final Logger logger = LoggerFactory.getLogger(IssueKeys.class);

    private static final char KEY_SEPARATOR = ',';

    private static final CacheLoader<String, Pattern> PATTERN_LOADER = new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String key) throws Exception {
            StringBuilder buffer = new StringBuilder("\\b(");
            if (Strings.isNullOrEmpty(key)) {
                // match all project keys
                buffer.append("[A-Z]+");
            } else {
                List<String> keys = Splitter.on(KEY_SEPARATOR).omitEmptyStrings().trimResults().splitToList(key);
                if (keys.size() == 1) {
                    // match only one project
                    buffer.append(keys.get(0));
                } else {
                    //create non capturing "or" group
                    buffer.append("(?:");
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        buffer.append(it.next());
                        if (it.hasNext()) {
                            buffer.append('|');
                        }
                    }
                    buffer.append(')');
                }
            }
            String pattern = buffer.append("-\\d+)").toString();
            logger.trace("created pattern {} for configuration key {}", pattern, key);
            return Pattern.compile(pattern);
        }
    };

    private static final LoadingCache<String, Pattern> PATTERN_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build(PATTERN_LOADER);

    private IssueKeys() {
    }

    /**
     * Creates and regex {@link Pattern} for the given comma separated list of issue keys. If the list is null or empty
     * an pattern which matches every jira issues will be returned. The created pattern is cached to increase the 
     * overall matching performance.
     * 
     * @param commaSeparatedIssueKeys comma separated list of jira project keys
     * 
     * @return regex pattern which matches the given jira projects
     */
    public static Pattern createPattern(String commaSeparatedIssueKeys) {
        return PATTERN_CACHE.getUnchecked(Strings.nullToEmpty(commaSeparatedIssueKeys).trim());
    }

    /**
     * Shorthand method for {@link #createPattern(java.lang.String)} with {@link JiraConfiguration#getFilter()}.
     * 
     * @param configuration jira configuration
     * 
     * @return regex pattern which matches the configured jira projects
     */
    public static Pattern createPattern(JiraConfiguration configuration) {
        return createPattern(configuration.getFilter());
    }

}
