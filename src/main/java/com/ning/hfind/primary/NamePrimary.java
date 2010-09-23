/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.hfind.primary;

import com.ning.hfind.FileAttributes;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

class NamePrimary implements Primary
{
    private Pattern pattern;
    PatternMatcher matcher = new Perl5Matcher();

    public NamePrimary(String namePattern) throws MalformedPatternException
    {
        GlobCompiler compiler = new GlobCompiler();
        this.pattern = compiler.compile(namePattern);
    }

    @Override
    public boolean passesFilter(FileAttributes attributes)
    {
        String[] fullPath = StringUtils.split(attributes.getPath(), "/");
        String filename = fullPath[fullPath.length - 1];

        return matcher.matches(filename, pattern);
    }

    @Override
    public String toString()
    {
        return "name";
    }
}
