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
package sonia.scm.jira;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.jira.config.JiraConfiguration;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Util methods for handling jira issue keys.
 *
 * @author Sebastian Sdorra <s.sdorra@gmail.com>
 */
public final class IssueKeys {

    private static final Logger logger = LoggerFactory.getLogger(IssueKeys.class);

    private static final char KEY_SEPARATOR = ',';

    public static final UnaryOperator<String> PATTERN_EXPRESSION_LOADER = rawKey -> {
        String key = Strings.nullToEmpty(rawKey).trim();
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
        buffer.append("-\\d+)");
        return buffer.toString();
    };

    private static final CacheLoader<String, Pattern> PATTERN_LOADER = new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String key) {
            String pattern = PATTERN_EXPRESSION_LOADER.apply(key);
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
