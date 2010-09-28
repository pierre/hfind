package com.ning.hfind;

import com.ning.hfind.primary.Expression;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPrinter
{
    private static final Character NULL_CHARACTER = '\u0000';

    @Test(groups = "fast")
    public void testDeleteIsOffByDefault() throws Exception
    {
        PrinterConfig config = new PrinterConfig();
        Assert.assertFalse(config.deleteMode());
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
        FsItem item = new StubFsItem(pathName);
        Printer printer = new Printer(item, Expression.TRUE, config);

        String formattedName = printer.preparePathNameForPrinting(item);
        Assert.assertEquals(formattedName.charAt(formattedName.length() - 1), lastCharacter.charValue());
    }
}
