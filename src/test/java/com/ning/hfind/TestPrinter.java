package com.ning.hfind;

import com.ning.hfind.primary.Expression;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPrinter
{
    private static final Character NULL_CHARACTER = '\u0000';

    class SilentPrinter extends Printer
    {
        private String lastPathName = null;

        public SilentPrinter(FsItem item, Expression expression, PrinterConfig config)
        {
            super(item, expression, config);
        }

        @Override
        protected void print(String pathName, boolean ignored)
        {
            lastPathName = pathName;
        }

        public String getLastPathName()
        {
            return lastPathName;
        }
    }

    @Test(groups = "fast")
    public void testPrint0() throws Exception
    {
        PrinterConfig config = new PrinterConfig();
        config.setEndLineWithNull(true);

        testOneItem("/user/pierre/mouraf.org/", config, NULL_CHARACTER);
        testOneItem(String.format("/user/pierre/mouraf.org/%s", NULL_CHARACTER), config, NULL_CHARACTER);
        testOneItem(String.format("/user/     pierre    %s    /mouraf.org/", NULL_CHARACTER), config, NULL_CHARACTER);
    }

    private void testOneItem(String pathName, PrinterConfig config, Character lastCharacter)
    {
        SilentPrinter printer = new SilentPrinter(new StubFsItem(pathName), Expression.TRUE, config);
        printer.run();

        Assert.assertEquals(printer.getLastPathName().charAt(printer.getLastPathName().length() - 1), lastCharacter.charValue());
    }
}
