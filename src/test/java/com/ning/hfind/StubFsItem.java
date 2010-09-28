package com.ning.hfind;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.RawLocalFileSystem;

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
    public String getName()
    {
        return pathName;
    }

    @Override
    public FileStatus getStatus()
    {
        return null;
    }

    @Override
    public FileSystem getFs()
    {
        return new RawLocalFileSystem();
    }

    @Override
    public boolean delete()
    {
        return false;
    }
}
