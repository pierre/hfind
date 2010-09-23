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

import org.apache.commons.lang.StringUtils;

class OperandModifier
{
    private final MODIFIED modified;
    private final Integer sanitizedArgument;

    public enum MODIFIED
    {
        LESS_THAN, EXACTLY, MORE_THAN
    }

    private final String MORE_THAN_CHARACTER = "+";
    private final String LESS_THAN_CHARACTER = "-";

    public OperandModifier(String argument)
    {
        if (StringUtils.startsWith(argument, MORE_THAN_CHARACTER)) {
            modified = MODIFIED.MORE_THAN;
            sanitizedArgument = Integer.valueOf(argument.substring(1, argument.length()));
        }
        else if (StringUtils.startsWith(argument, LESS_THAN_CHARACTER)) {
            modified = MODIFIED.LESS_THAN;
            sanitizedArgument = Integer.valueOf(argument.substring(1, argument.length()));
        }
        else {
            modified = MODIFIED.EXACTLY;
            sanitizedArgument = Integer.valueOf(argument);
        }
    }

    public boolean evaluate(int a, double b)
    {
        switch (modified) {
            case LESS_THAN:
                return a < b;
            case EXACTLY:
                return a == b;
            case MORE_THAN:
                return a > b;
            default:
                throw new IllegalStateException(String.format("Unknown modifier: %s", modified));
        }
    }

    public boolean evaluate(int a, long b)
    {
        switch (modified) {
            case LESS_THAN:
                return a < b;
            case EXACTLY:
                return a == b;
            case MORE_THAN:
                return a > b;
            default:
                throw new IllegalStateException(String.format("Unknown modifier: %s", modified));
        }
    }


    public Integer getSanitizedArgument()
    {
        return sanitizedArgument;
    }
}
