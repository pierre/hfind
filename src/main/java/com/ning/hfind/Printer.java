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
    private final FsItem topLevelItem;
    private final Expression expression;

    private final boolean deleteMode;
    private final boolean depthMode;
    private final boolean endLineWithNull;
    private final boolean verbose;

    public Printer(FsItem topLevelItem, Expression expression, PrinterConfig config)
    {
        this.topLevelItem = topLevelItem;
        this.expression = expression;

        this.deleteMode = config.deleteMode();
        this.depthMode = config.depthMode();
        this.endLineWithNull = config.endLineWithNull();
        this.verbose = config.verbose();
    }

    public void run()
    {
        run(topLevelItem);
    }

    private void run(FsItem item)
    {
        FileStatusAttributes itemAttributes = item.getFileStatusAttributes();
        if (itemAttributes == null) {
            return;
        }

        if (!depthMode) {
            if (expression.evaluate(itemAttributes)) {
                print(item, endLineWithNull);
            }
        }

        for (FsItem childItem : item.getChildren()) {
            run(childItem);
        }

        if (depthMode) {
            if (expression.evaluate(itemAttributes)) {
                print(item, endLineWithNull);
            }
        }
    }

    private void print(FsItem currentItem, boolean dontAddANewLine)
    {
        String pathName = preparePathNameForPrinting(currentItem);

        if (deleteMode) {
            if (currentItem.delete() && verbose) {
                System.out.println(String.format("Deleted %s", pathName));
            }
        }
        else {
            if (dontAddANewLine) {
                System.out.print(pathName);
            }
            else {
                System.out.println(pathName);
            }
        }
    }

    protected String preparePathNameForPrinting(FsItem currentItem)
    {
        String pathName;
        if (verbose) {
            pathName = currentItem.getFullName();
        }
        else {
            pathName = currentItem.getName();
        }

        if (endLineWithNull) {
            pathName = String.format("%s\0", pathName);
        }
        return pathName;
    }
}