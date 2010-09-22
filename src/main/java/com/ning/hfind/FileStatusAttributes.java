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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import java.io.IOException;
import java.util.Collection;

public class FileStatusAttributes implements FileAttributes
{

    private final FileSystem fs;
    private final FileStatus status;

    public FileStatusAttributes(FileSystem fs, FileStatus status)
    {
        this.fs = fs;
        this.status = status;
    }

    @Override
    public ReadableDateTime getModificationDate()
    {
        return new DateTime(status.getModificationTime());
    }

    @Override
    public boolean isDirectory()
    {
        return status.isDir();
    }

    @Override
    public long getLength()
    {
        return status.getLen();
    }

    @Override
    public long getBlockSize()
    {
        return status.getBlockSize();
    }

    @Override
    public int getReplicationCount()
    {
        return status.getReplication();
    }

    @Override
    public String getOwner()
    {
        return status.getOwner();
    }

    @Override
    public String getGroup()
    {
        return status.getGroup();
    }

    @Override
    public String getPath()
    {
        return status.getPath().toUri().getPath();
    }

    @Override
    public boolean isReadableBy(String user, Collection<String> groups)
    {
        return allows(FsAction.READ, user, groups);
    }

    @Override
    public boolean isWritableBy(String user, Collection<String> groups)
    {
        return allows(FsAction.WRITE, user, groups);
    }

    @Override
    public boolean isExecutableBy(String user, Collection<String> groups)
    {
        return allows(FsAction.EXECUTE, user, groups);
    }

    @Override
    public FileStatus[] children() throws IOException
    {
        if (isDirectory()) {
            return fs.listStatus(status.getPath());
        }
        else {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s:modified=%s,type=%s,length=%d", getPath(), getModificationDate(), isDirectory() ? "dir" : "file", getLength());
    }

    @Override
    public int compareTo(FileAttributes rhs)
    {
        return getPath().compareTo(rhs.getPath());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileStatusAttributes that = (FileStatusAttributes) o;

        return getPath().equals(that.getPath());

    }

    @Override
    public int hashCode()
    {
        return getPath().hashCode();
    }

    private boolean allows(FsAction action, String user, Collection<String> groups)
    {
        FsPermission permission = status.getPermission();

        return permission.getOtherAction().implies(action) ||
            status.getOwner().equals(user) && permission.getUserAction().implies(action) ||
            groups.contains(status.getGroup()) && permission.getGroupAction().implies(action);
    }
}