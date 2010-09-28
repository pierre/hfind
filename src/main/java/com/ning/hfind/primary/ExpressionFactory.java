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

import com.ning.hfind.util.PushbackIterator;
import org.apache.commons.cli.Option;
import org.apache.oro.text.regex.MalformedPatternException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ExpressionFactory
{
    public static Expression buildExpressionFromCommandLine(PushbackIterator<Option> iterator)
    {
        if (!iterator.hasNext()) {
            return Expression.TRUE;
        }
        else {
            try {
                return build(iterator);
            }
            catch (MalformedPatternException e) { // from -name
                throw new IllegalArgumentException(e);
            }
        }
    }

    private static Expression build(PushbackIterator<Option> iterator) throws MalformedPatternException
    {
        // Extract leftmost primary from the expression
        Primary leftPrimary = PrimaryFactory.primaryFromOption(iterator.next());

        // We ignore certain primaries
        if (leftPrimary == null) {
            return build(iterator);
        }

        if (!iterator.hasNext()) {
            return new Expression(leftPrimary, Primary.ALWAYS_MATCH, new AndOperand());
        }

        // Extract operand
        Operand operand = OperandFactory.operandFromOption(iterator);

        // If it's an OR, compute
        //     leftPrimary OR (rest of the command line)
        if (operand.getClass().equals(OrOperand.class)) {
            return new Expression(leftPrimary, build(iterator), operand);
        }

        // It's an AND, regardless if -a was explicitly specified
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Invalid expression");
        }

        // Extract right primary
        Primary rightPrimary = PrimaryFactory.primaryFromOption(iterator.next());

        Expression andExpression = new Expression(leftPrimary, rightPrimary, new AndOperand());

        if (!iterator.hasNext()) {
            return andExpression;
        }
        else {
            // Extract next operand
            operand = OperandFactory.operandFromOption(iterator);
            return new Expression(andExpression, build(iterator), operand);
        }
    }

    public static Iterator<Option> sanitizeCommandLine(Option[] options)
    {
        ArrayList<Option> optionsList = new ArrayList<Option>(Arrays.asList(options));

       Iterator<Option> optionIterator = (Iterator<Option>) optionsList.iterator();
        while (optionIterator.hasNext()) {
            Option option =  optionIterator.next();

            try {
                if (PrimaryFactory.primaryFromOption(option) == null) {
                    optionIterator.remove();
                }
            }
            catch (MalformedPatternException ignored) {
            }
            catch (IllegalArgumentException ignored) {
            }
        }

        return optionsList.iterator();
    }
}
