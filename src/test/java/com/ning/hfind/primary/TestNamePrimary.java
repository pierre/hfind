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

import com.ning.hfind.StubFileAttributes;
import org.apache.oro.text.regex.MalformedPatternException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNamePrimary
{
    @Test(groups = "fast")
    public void testPatternMatching() throws Exception
    {
        Assert.assertTrue(testOneMatch("pierre*", "pierreR0cks"));
        Assert.assertTrue(testOneMatch("*pierre*", "pierreR0cks"));
        Assert.assertTrue(testOneMatch("*pierre*", "noReallYpierreR0cks"));
        Assert.assertTrue(testOneMatch("*.thrift", "my.thrift"));
        Assert.assertFalse(testOneMatch("*.thrift", "mythrift"));
        Assert.assertTrue(testOneMatch("[mM][yY][fF][iI][lL][eE]*", "mYfIle"));
    }

    private boolean testOneMatch(String patternToTest, String path) throws MalformedPatternException
    {
        Primary namePrimary = new NamePrimary(patternToTest);

        StubFileAttributes fileAttributes = new StubFileAttributes();
        fileAttributes.setPath(path);

        return namePrimary.passesFilter(fileAttributes);
    }
}
