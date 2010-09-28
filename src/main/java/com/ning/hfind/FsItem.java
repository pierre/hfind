package com.ning.hfind;

import com.google.common.collect.ImmutableList;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

public interface FsItem
{
    ImmutableList<FsItem> getChildren();

    String getName();

    FileStatus getStatus();

    FileSystem getFs();
}
