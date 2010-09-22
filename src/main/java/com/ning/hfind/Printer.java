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

import com.ning.hfind.filter.Expression;

public class Printer
{
    public Printer(String path, HdfsItem item, Expression expression)
    {
        run(item, expression, path);
    }

    private void run(HdfsItem item, Expression expression, String pathPrefix)
    {
        FileStatusAttributes itemAttributes = new FileStatusAttributes(item.getStatus());

        if (expression.evaluate(itemAttributes)) {
            String filePrefix = "";

            if (!itemAttributes.isDirectory()) {
                filePrefix = "/";
            }

            System.out.println(String.format("%s%s%s", pathPrefix, filePrefix, item.getName()));
        }

        if (itemAttributes.isDirectory()) {
            pathPrefix = String.format("%s%s", pathPrefix, item.getName());
        }

        for (HdfsItem childItem : item.getChildren()) {
            run(childItem, expression, pathPrefix);
        }
    }
}