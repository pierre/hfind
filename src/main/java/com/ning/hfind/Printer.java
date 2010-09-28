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

import com.ning.hfind.primary.Expression;

public class Printer
{
    private final FsItem item;
    private final Expression expression;
    private final boolean depthMode;
    private final boolean endLineWithNull;

    public Printer(FsItem item, Expression expression, PrinterConfig config)
    {
        this.item = item;
        this.expression = expression;
        this.depthMode = config.depthMode();
        this.endLineWithNull = config.endLineWithNull();
    }

    public void run()
    {
        run(item);
    }

    private void run(FsItem item)
    {
        FileStatusAttributes itemAttributes = new FileStatusAttributes(item.getFs(), item.getStatus());

        String pathName = item.getName();
        if (endLineWithNull) {
            pathName = String.format("%s\0", pathName);
        }

        if (!depthMode) {
            if (expression.evaluate(itemAttributes)) {
                print(pathName, endLineWithNull);
            }
        }

        for (FsItem childItem : item.getChildren()) {
            run(childItem);
        }

        if (depthMode) {
            if (expression.evaluate(itemAttributes)) {
                print(pathName, endLineWithNull);
            }
        }
    }

    protected void print(String pathName, boolean dontAddANewLine)
    {
        if (dontAddANewLine) {
            System.out.print(pathName);
        }
        else {
            System.out.println(pathName);
        }
    }
}