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

package com.ning.hfind;

import org.apache.hadoop.fs.FileStatus;
import org.joda.time.ReadableDateTime;

import java.io.IOException;
import java.util.Collection;

public class StubFileAttributes implements FileAttributes
{
    private String path;

    @Override
    public ReadableDateTime getAccessDate()
    {
        return null;
    }

    @Override
    public ReadableDateTime getModificationDate()
    {
        return null;
    }

    @Override
    public boolean isDirectory()
    {
        return false;
    }

    @Override
    public long getLength()
    {
        return 0;
    }

    @Override
    public long getBlockSize()
    {
        return 0;
    }

    @Override
    public int getReplicationCount()
    {
        return 0;
    }

    @Override
    public String getOwner()
    {
        return null;
    }

    @Override
    public String getGroup()
    {
        return null;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
    public boolean isReadableBy(String user, Collection<String> groups)
    {
        return false;
    }

    @Override
    public boolean isWritableBy(String user, Collection<String> groups)
    {
        return false;
    }

    @Override
    public boolean isExecutableBy(String user, Collection<String> groups)
    {
        return false;
    }

    @Override
    public FileStatus[] children() throws IOException
    {
        return new FileStatus[0];
    }

    @Override
    public int compareTo(FileAttributes fileAttributes)
    {
        return 0;
    }
}
