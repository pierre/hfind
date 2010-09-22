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
    private final boolean depthMode;

    public Printer(HdfsItem item, Expression expression, boolean depthMode)
    {
        this.depthMode = depthMode;
        run(item, expression);
    }

    private void run(HdfsItem item, Expression expression)
    {
        FileStatusAttributes itemAttributes = new FileStatusAttributes(item.getStatus());

        if (!depthMode) {
            if (expression.evaluate(itemAttributes)) {
                System.out.println(item.getName());
            }
        }

        for (HdfsItem childItem : item.getChildren()) {
            run(childItem, expression);
        }

        if (depthMode) {
            if (expression.evaluate(itemAttributes)) {
                System.out.println(item.getName());
            }
        }
    }
}