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
import org.apache.commons.lang.StringUtils;

public class SizePrimary implements Primary
{
    private final int size;

    private static final String SIZE_CHARACTER = "c";
    private boolean blockMode = true;
    private final OperandModifier operandModifier;

    public SizePrimary(String size)
    {
        if (StringUtils.endsWith(size, SIZE_CHARACTER)) {
            blockMode = false;
            size = StringUtils.chop(size);
        }

        operandModifier = new OperandModifier(size);
        this.size = operandModifier.getSanitizedArgument();
    }

    @Override
    public boolean passesFilter(FileAttributes attributes)
    {
        if (blockMode) {
            return operandModifier.evaluate(size, Math.ceil(attributes.getBlockSize() / 512.0));
        }
        else {
            return operandModifier.evaluate(size, attributes.getBlockSize());
        }
    }

    @Override
    public String toString()
    {
        return "size";
    }
}
