package com.ning.hfind.config;

import org.apache.hadoop.conf.Configuration;
import org.skife.config.ConfigurationObjectFactory;

public class HdfsConfig
{
    private final Configuration hdfsConfig;

    public HdfsConfig()
    {
        this.hdfsConfig = configureHDFSAccess();
    }

    private Configuration configureHDFSAccess()
    {
        HFindConfig hfindConfig = new ConfigurationObjectFactory(System.getProperties()).build(HFindConfig.class);
        Configuration hdfsConfig = new Configuration();

        hdfsConfig.set("fs.default.name", hfindConfig.getNamenodeUrl());
        hdfsConfig.set("hadoop.job.ugi", hfindConfig.getHadoopUgi());

        // Set the URI schema to file:///, this will make FileSystem.get return a LocalFileSystem instance
        if (hfindConfig.localMode()) {
            hdfsConfig.set("fs.default.name", "file:///");
        }

        return hdfsConfig;
    }

    public Configuration getConfiguration()
    {
        return hdfsConfig;
    }
}
