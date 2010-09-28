package com.ning.hfind;

import com.ning.hfind.config.HdfsConfig;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HdfsAccess
{
    private static HdfsAccess singletonHdfsAccess;

    private final HdfsConfig hdfsConfig;
    private FileSystem fs = null;

    public static synchronized HdfsAccess get()
    {
        if (singletonHdfsAccess == null) {
            singletonHdfsAccess = new HdfsAccess();
        }
        return singletonHdfsAccess;
    }

    private HdfsAccess()
    {
        hdfsConfig = new HdfsConfig();
    }

    public FileStatus getFileStatus(Path path) throws IOException
    {
        return getFileSystem().getFileStatus(path);
    }

    public FileStatus[] listStatus(Path path) throws IOException
    {
        return getFileSystem().listStatus(path);
    }

    public boolean delete(Path path) throws IOException
    {
        return getFileSystem().delete(path, true);
    }

    public FileStatusAttributes getFileStatusAttributes(Path path) throws IOException
    {
        return new FileStatusAttributes(getFileSystem(), getFileStatus(path));
    }

    private void setFileSystem() throws IOException
    {
        fs = FileSystem.get(hdfsConfig.getConfiguration());
    }

    private FileSystem getFileSystemSafe() throws IOException
    {
        fs.getFileStatus(new Path("/"));
        return fs;
    }

    private FileSystem getFileSystem() throws IOException
    {
        if (fs == null) {
            setFileSystem();
        }

        try {
            return getFileSystemSafe();
        }
        catch (NullPointerException e) {
            // Connection closed?
            // There is a weird bug in deep trees, where eventually the fs.getFileStatus throws a NullPointerException
            // Try to go around it by forcing a refresh of fs.
            setFileSystem();

            try {
                return getFileSystemSafe();
            }
            catch (NullPointerException f) {
                throw new IOException(String.format("Lost connection to HDFS: %s", f.getLocalizedMessage()));
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }
}
