/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.hfind;

import com.ning.hfind.config.HFindConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.skife.config.ConfigurationObjectFactory;

import java.io.IOException;

public class Find
{
    private static final Options options = new Options();
    private static FileSystem fs;

    static {
        options.addOption("h", "help", false, "print this message");
    }

    public static void usage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("hfind [-H | -L] path ... [operand_expression ...]", options);
    }

    private static Configuration configureHDFSAccess(HFindConfig config)
    {
        Configuration conf = new Configuration();

        conf.set("fs.default.name", config.getNamenodeUrl());
        conf.set("hadoop.job.ugi", config.getHadoopUgi());

        return conf;
    }

    public static void main(String[] args) throws ParseException, IOException
    {
        HFindConfig hfindConfig = new ConfigurationObjectFactory(System.getProperties()).build(HFindConfig.class);
        Configuration hadoopConfig = configureHDFSAccess(hfindConfig);
        fs = FileSystem.get(hadoopConfig);

        CommandLineParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args);
        args = line.getArgs();

        if (line.hasOption("help") || args.length == 0) {
            usage();
            return;
        }

        String path = args[0];

        run(path);

        System.exit(0);
    }

    private static void run(String path) throws IOException
    {
        FileAttributesFilter filter = FileAttributesFilter.DEFAULT;
        HdfsItem listing = new HdfsItem(fs, path);

        new Printer(listing, filter);
    }
}
