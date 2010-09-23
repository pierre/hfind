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

package com.ning.hfind.filter;

import com.ning.hfind.FileAttributes;

class GroupPrimary implements Primary
{
    private final String group;

    public GroupPrimary(String group)
    {
        this.group = group;
    }

    @Override
    public boolean passesFilter(FileAttributes attributes)
    {
        return attributes.getGroup().equals(group);
    }
    
    @Override
    public String toString()
    {
        return "group";
    }
}
