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
import com.ning.hfind.primary.Expression;
import com.ning.hfind.primary.ExpressionFactory;
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

    public static final String AND = "a";
    public static final String OR = "o";

    private static final int COMMAND_LINE_ERROR = 2;
    private static final int HADOOP_ERROR = 3;

    /**
     * Options which don't make any sense in Hadoop:
     *
     * -H Cause the file information and file type evaluated for each symbolic link encountered on the command line to be those of the file referenced by the link, and not the link itself. If the referenced file does not exist, the file information and type shall be for the link itself. File information for all symbolic links not on the command line shall be that of the link itself.
     * -L Cause the file information and file type evaluated for each symbolic link to be those of the file referenced by the link, and not the link itself.
     * -xdev The primary shall always evaluate as true; it shall cause find not to continue descending past directories that have a different device ID ( st_dev, see the stat() function defined in the System Interfaces volume of IEEE Std 1003.1-2001). If any -xdev primary is specified, it shall apply to the entire expression even if the -xdev primary would not normally be evaluated.
     * -type only implements 'f' or 'd'
     * -links The primary shall evaluate as true if the file has n links.
     * -ctime The primary shall evaluate as true if the time of last change of file status information subtracted from the initialization time, divided by 86400 (with any remainder discarded), is n.
     *
     * TODO: -exec, -ok
     */
    static {
        options.addOption("h", "help", false, "Print this message");

        options.addOption(AND, null, false, "AND operator");
        options.addOption(OR, null, false, "OR operator");

        options.addOption("name", null, true, "True if the last component of the pathname being examined matches pattern");
        options.addOption("nouser", null, false, "True if the file belongs to an unknown user");
        options.addOption("nogroup", null, false, "True if the file belongs to an unknown group");
        options.addOption("prune", null, false, "This primary always evaluates to true. It causes find to not descend into the current file." +
            "Note, the -prune primary has no effect if the -depth option was specified.");
        options.addOption("perm", null, true, "The mode argument is used to represent file mode bits. It shall be identical in format to the symbolic_mode operand described in chmod() , and shall be interpreted as follows. To start, a template shall be assumed with all file mode bits cleared. An op symbol of '+' shall set the appropriate mode bits in the template; '-' shall clear the appropriate bits; '=' shall set the appropriate mode bits, without regard to the contents of process' file mode creation mask. The op symbol of '-' cannot be the first character of mode; this avoids ambiguity with the optional leading hyphen. Since the initial mode is all bits off, there are not any symbolic modes that need to use '-' as the first character.\n" +
            "\n" +
            "If the hyphen is omitted, the primary shall evaluate as true when the file permission bits exactly match the value of the resulting template.\n" +
            "\n" +
            "Otherwise, if mode is prefixed by a hyphen, the primary shall evaluate as true if at least all the bits in the resulting template are set in the file permission bits." +
            "If the hyphen is omitted, the primary shall evaluate as true when the file permission bits exactly match the value of the octal number onum and only the bits corresponding to the octal mask 07777 shall be compared. (See the description of the octal mode in chmod().) Otherwise, if onum is prefixed by a hyphen, the primary shall evaluate as true if at least all of the bits specified in onum that are also set in the octal mask 07777 are set.");
        options.addOption("type", null, true, "The primary shall evaluate as true if the type of the file is arg, where arg is 'd' or 'f' for directory or regular file, respectively");
        options.addOption("user", null, true, "The primary shall evaluate as true if the file belongs to the specified user");
        options.addOption("group", null, true, "The primary shall evaluate as true if the file belongs to the specified group");
        options.addOption("size", null, true, "The primary shall evaluate as true if the file size in bytes, divided by 512 and rounded up to the next integer, is n. If n is followed by the character 'c', the size shall be in bytes");
        options.addOption("atime", null, true, "The primary shall evaluate as true if the file access time subtracted from the initialization time, divided by 86400 (with any remainder discarded), is arg");
        options.addOption("mtime", null, true, "The primary shall evaluate as true if the file modification time subtracted from the initialization time, divided by 86400 (with any remainder discarded), is arg");
        options.addOption("print", null, false, "The primary shall always evaluate as true; it shall cause the current pathname to be written to standard output");
        options.addOption("newer", null, true, "The primary shall evaluate as true if the modification time of the current file is more recent than the modification time of the file named by the pathname file");

        // Note: this is NOT the POSIX -depth option. See BSD implementation.
        options.addOption("depth", null, true, "Depth of the recursion crawl");
        options.addOption("d", null, false, "Cause find to perform a depth-first traversal");

        // Extra, non POSIX, primaries
        options.addOption("empty", null, false, "True if the current file or directory is empty");
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

    private static void connectToHDFS() throws IOException
    {
        HFindConfig hfindConfig = new ConfigurationObjectFactory(System.getProperties()).build(HFindConfig.class);
        Configuration hadoopConfig = configureHDFSAccess(hfindConfig);
        fs = FileSystem.get(hadoopConfig);
    }

    public static void main(String[] args) throws ParseException, IOException
    {
        CommandLineParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args);
        args = line.getArgs();

        if (args.length > 1) {
            // find(1) seems to complain about the first argument only, let's do the same
            System.out.println(String.format("hfind: %s: unknown option", args[1]));
            System.exit(COMMAND_LINE_ERROR);
        }
        if (line.hasOption("help") || args.length == 0) {
            usage();
            return;
        }

        String path = args[0];
        // Optimization: check the depth on a top-level basis, not on a per-file basis
        // This avoids crawling files on Hadoop we don't care about
        int depth = Integer.MAX_VALUE;
        if (line.hasOption("depth")) {
            String depthOptionValue = line.getOptionValue("depth");
            depth = Integer.valueOf(depthOptionValue);
        }
        boolean depthMode = false;
        if (line.hasOption("d")) {
            depthMode = true;
        }

        Expression expression = null;
        try {
            expression = ExpressionFactory.buildExpressionFromCommandLine(line.getOptions());
        }
        catch (IllegalArgumentException e) {
            System.err.println(e);
            System.exit(COMMAND_LINE_ERROR);
        }

        try {
            connectToHDFS();
            expression.run(path, fs, depth, depthMode);

            System.exit(0);
        }
        catch (IOException e) {
            System.err.println(String.format("Error crawling HDFS: %s", e.getLocalizedMessage()));
            System.exit(HADOOP_ERROR);
        }
    }
}
