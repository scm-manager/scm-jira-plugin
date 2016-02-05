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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Util method to simplify auto close word handling.
 * 
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class AutoCloseWords {

    private static final CacheLoader<String,Pattern> PATTERN_LOADER = new CacheLoader<String, Pattern>() {
        @Override
        public Pattern load(String key) throws Exception {
            String[] words = key.split("\\s");
            StringBuilder buffer = new StringBuilder("([^A-Za-z0-9]|^)");
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    buffer.append("\\s*");
                }
                buffer.append(words[i]);
            }
            buffer.append("([^A-Za-z0-9]|$)");
            return Pattern.compile(buffer.toString(), Pattern.CASE_INSENSITIVE);
        }
    };
    
    private static final LoadingCache<String, Pattern> PATTERN_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build(PATTERN_LOADER);
    
    private AutoCloseWords(){
    }
   
    /**
     * Returns {@code true} if the auto word could be found in the description.
     * 
     * @param description changeset description
     * @param word auto close word
     * 
     * @return {@code true} if the auto close word is found
     */
    public static boolean find(String description, String word){
        Pattern pattern = PATTERN_CACHE.getUnchecked(word.trim());
        return pattern.matcher(description).find();
    }

}
