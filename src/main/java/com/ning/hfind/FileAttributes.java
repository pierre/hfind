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

public interface FileAttributes extends Comparable<FileAttributes>
{
    public ReadableDateTime getModificationDate();

    public boolean isDirectory();

    public long getLength();

    public long getBlockSize();

    public int getReplicationCount();

    public String getOwner();

    public String getGroup();

    public String getPath();

    public boolean isReadableBy(String user, Collection<String> groups);

    public boolean isWritableBy(String user, Collection<String> groups);

    public boolean isExecutableBy(String user, Collection<String> groups);

    public FileStatus[] children() throws IOException;
}
