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

import com.ning.hfind.Find;
import org.apache.commons.cli.Option;

public class ExpressionFactory
{
    public static Expression buildExpressionFromCommandLine(Option[] options)
    {
        if (options.length == 0) {
            return new Expression(Primary.ALWAYS_MATCH, Primary.ALWAYS_MATCH, new AndOperand());
        }
        else {
            return buildExpressionFromCommandLine(options, 0);
        }
    }

    private static Expression buildExpressionFromCommandLine(Option[] options, int index)
    {
        Primary leftPrimary = PrimaryFactory.primaryFromOption(options[index]);
        index++;

        // We ignore certain primaries, e.g. -depth
        if (leftPrimary == null) {
            leftPrimary = Primary.ALWAYS_MATCH;
        }

        if (index >= options.length) {
            return new Expression(leftPrimary, Primary.ALWAYS_MATCH, new AndOperand());
        }
        Option o = options[index];

        Operand operand;
        if (o.getOpt().equals(Find.OR)) {
            operand = new OrOperand();
            index++;
        }
        else if (o.getOpt().equals(Find.AND)) {
            operand = new AndOperand();
            index++;
        }
        else {
            operand = new AndOperand();
        }

        if (index >= options.length) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return new Expression(leftPrimary, buildExpressionFromCommandLine(options, index), operand);
    }
}
