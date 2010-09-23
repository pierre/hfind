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

class TypePrimary implements Primary
{
    private final String type;

    private static final String TYPE_DIRECTORY = "d";
    private static final String TYPE_FILE = "f";

    public TypePrimary(String type)
    {
        if (!type.equals(TYPE_DIRECTORY) && !type.equals(TYPE_FILE)) {
            throw new IllegalArgumentException(String.format("File type must be %s or %s", TYPE_DIRECTORY, TYPE_FILE));
        }

        this.type = type;
    }

    @Override
    public boolean passesFilter(FileAttributes attributes)
    {
        return (attributes.isDirectory() && type.equals(TYPE_DIRECTORY)) || (!attributes.isDirectory() && type.equals(TYPE_FILE));
    }

    @Override
    public String toString()
    {
        return "type";
    }
}
