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
import com.ning.hfind.HdfsItem;
import com.ning.hfind.Printer;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;

/**
 * An expression is a combination of two primaries (or a primary and an expression) and an operand, i.e.
 * <p/>
 * LEFT OP RIGHT
 * <p/>
 * where LEFT and RIGHT are primaries (RIGHT may be an expression) and OP an operand.
 */
public class Expression
{
    private final Expression expressionRight;

    private final Primary primaryLeft;
    private final Primary primaryRight;

    private final Operand operand;

    /**
     * Base expression
     *
     * @param primaryLeft  primary on the left of the operand (higher precedence)
     * @param primaryRight primary on the right of the operand
     * @param operand      operand evaluating primaryLeft OP primaryRight
     */
    public Expression(
        Primary primaryLeft,
        Primary primaryRight,
        Operand operand
    )
    {
        this.expressionRight = null;

        this.primaryLeft = primaryLeft;
        this.operand = operand;
        this.primaryRight = primaryRight;
    }

    public Expression(
        Primary primaryLeft,
        Expression expressionRight,
        Operand operand
    )
    {
        this.primaryRight = null;

        this.primaryLeft = primaryLeft;
        this.operand = operand;
        this.expressionRight = expressionRight;
    }

    public void run(String path, FileSystem fs) throws IOException
    {
        HdfsItem listing = new HdfsItem(fs, path);

        new Printer(listing, this);
    }
    
    @Override
    public String toString()
    {
        if (primaryRight != null) {
            return String.format("(%s %s %s)", primaryLeft, operand, primaryRight);
        }
        else {
            return String.format("%s %s (%s)", primaryLeft, operand, expressionRight);
        }
    }

    public boolean evaluate(FileAttributes fileAttributes)
    {
        if (primaryRight != null) {
            return operand.evaluateOperand(primaryLeft, primaryRight, fileAttributes);
        }
        else {
            return operand.evaluateOperand(primaryLeft, expressionRight, fileAttributes);
        }
    }
}
