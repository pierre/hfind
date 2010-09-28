package com.ning.hfind;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

public class StubFsItem implements FsItem
{
    private final String name;

    public StubFsItem(String name)
    {
        this.name = name;
    }

    @Override
    public ImmutableList<FsItem> getChildren()
    {
        return ImmutableList.of();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public FileStatus getStatus()
    {
        return null;
    }

    @Override
    public FileSystem getFs()
    {
        return null;
    }
}
