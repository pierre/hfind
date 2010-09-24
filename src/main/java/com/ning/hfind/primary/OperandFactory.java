package com.ning.hfind.primary;

import com.ning.hfind.Find;
import com.ning.hfind.util.PushbackIterator;
import org.apache.commons.cli.Option;

public class OperandFactory
{
    public static Operand operandFromOption(PushbackIterator<Option> iterator)
    {
        Option o = iterator.next();
        if (o.getOpt().equals(Find.OR)) {
            return new OrOperand();
        }
        else if (o.getOpt().equals(Find.AND)) {
            return new AndOperand();
        }
        else {
            // Implied AND (two primaries next to each other
            iterator.pushBack();
            return new AndOperand();
        }
    }
}
