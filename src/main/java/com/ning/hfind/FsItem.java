package com.ning.hfind;

import com.google.common.collect.ImmutableList;

public interface FsItem
{
    ImmutableList<FsItem> getChildren();

    String getFullName();

    String getName();

    boolean delete();

    FileStatusAttributes getFileStatusAttributes();
}
