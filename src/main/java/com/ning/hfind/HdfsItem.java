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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;

public class HdfsItem implements FsItem
{
    private final static Logger log = Logger.getLogger(HdfsItem.class.getName());

    private final FileSystem fs;
    private final FileStatus status;

    private final Path path;
    private final int depth;

    private final String name;

    private volatile ImmutableList<FsItem> children;

    public HdfsItem(FileSystem fs, String path, int depth) throws IOException
    {
        this(fs, fs.getFileStatus(new Path(path)), depth);
    }

    private HdfsItem(FileSystem fs, FileStatus status, int depth) throws IOException
    {
        this.fs = fs;
        this.status = status;

        this.path = status.getPath();
        this.depth = depth;

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
                for (FileStatus status : fs.listStatus(path)) {
                    children.add(new HdfsItem(fs, status, depth - 1));
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
    public String getName()
    {
        return name;
    }

    @Override
    public FileStatus getStatus()
    {
        return status;
    }

    @Override
    public FileSystem getFs()
    {
        return fs;
    }

    @Override
    public boolean delete()
    {
        try {
            fs.delete(path, true);
            return true;
        }
        catch (IOException e) {
            System.err.println(String.format("Unable to delete: %s (%s)", name, e.getLocalizedMessage()));
            return false;
        }
    }
}
