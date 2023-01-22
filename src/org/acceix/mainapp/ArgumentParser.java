/*
 * The MIT License
 *
 * Copyright 2022 Rza Asadov (rza at asadov dot me).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.acceix.mainapp;

import java.text.ParseException;
import java.util.LinkedHashMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author zrid
 */
public class ArgumentParser {
    
    public static LinkedHashMap parseArguments(String[] args) throws ParseException, org.apache.commons.cli.ParseException {
        
        LinkedHashMap<String,String> returnValue = new LinkedHashMap<>();

        Option configOption = Option.builder("c")
                                    .longOpt("conf")
                                    .required(false)
                                    .numberOfArgs(1)
                                    .desc("path to config file")
                                    .type(String.class)
                                    .build();
        
        Option appOption = Option.builder("a")
                                    .longOpt("app")
                                    .required(false)
                                    .numberOfArgs(1)
                                    .desc("app name")
                                    .type(String.class)
                                    .build();        

        Options options = new Options();
        options.addOption(configOption);
        options.addOption(appOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(options, args, true);

        if (cmdLine.hasOption("c")) {
            returnValue.put("CONFIG_FILE_PATH", cmdLine.getParsedOptionValue("c").toString());
        } 
        
        if (cmdLine.hasOption("a")) {
            returnValue.put("APP_NAME", cmdLine.getParsedOptionValue("a").toString());
        }         
        
        return returnValue;

    }        
    
}
