package com.ning.hfind;

import com.google.common.collect.ImmutableList;

public class StubFsItem implements FsItem
{
    private final String pathName;

    public StubFsItem(String pathName)
    {
        this.pathName = pathName;
    }

    @Override
    public ImmutableList<FsItem> getChildren()
    {
        return ImmutableList.of();
    }

    @Override
    public String getFullName()
    {
        return pathName;
    }

    @Override
    public String getName()
    {
        return pathName;
    }

    @Override
    public boolean delete()
    {
        return false;
    }

    @Override
    public FileStatusAttributes getFileStatusAttributes()
    {
        return null;
    }
}
