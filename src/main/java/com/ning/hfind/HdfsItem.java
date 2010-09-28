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

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HdfsItem implements FsItem
{
    private final HdfsAccess hdfsAccess;
    private final int depth;
    private final String name;

    private final Path path;

    private volatile ImmutableList<FsItem> children;

    public HdfsItem(HdfsAccess hdfsAccess, String path, int depth) throws IOException
    {
        this(hdfsAccess, hdfsAccess.getFileStatus(new Path(path)), depth);
    }

    private HdfsItem(HdfsAccess hdfsAccess, FileStatus status, int depth) throws IOException
    {
        this.hdfsAccess = hdfsAccess;
        this.depth = depth;

        this.path = status.getPath();

        if (status.isDir()) {
            this.name = path.toUri().getPath();
        }
        else {
            this.name = String.format("%s/%s", path.getParent().toUri().getPath(), path.getName());
            this.children = ImmutableList.of();
        }
    }

    @Override
    public ImmutableList<FsItem> getChildren()
    {
        if (depth <= 0) {
            children = ImmutableList.of();
        }
        else if (children == null) {
            ImmutableList.Builder<FsItem> children = ImmutableList.builder();

            try {
                for (FileStatus status : hdfsAccess.listStatus(path)) {
                    children.add(new HdfsItem(hdfsAccess, status, depth - 1));
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.children = children.build();
        }

        return children;
    }

    @Override
    public String getFullName()
    {
        return path.toString();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean delete()
    {
        try {
            hdfsAccess.delete(path);
            return true;
        }
        catch (IOException e) {
            System.err.println(String.format("Unable to delete: %s (%s)", name, e.getLocalizedMessage()));
            return false;
        }
    }

    @Override
    public FileStatusAttributes getFileStatusAttributes()
    {
        try {
            return hdfsAccess.getFileStatusAttributes(path);
        }
        catch (IOException e) {
            System.err.println(String.format("Unable to access path: %s", path));
            return null;
        }
    }
}
