/*
 * Copyright 2015 Yahoo Inc.
 * Licensed under the terms of the Apache 2 license. Please see LICENSE file in the project root for terms.
 */
package com.yahoo.validatar.report.junit;

import com.yahoo.validatar.common.Helpable;
import com.yahoo.validatar.common.Query;
import com.yahoo.validatar.common.Test;
import com.yahoo.validatar.common.TestSuite;
import com.yahoo.validatar.report.Formatter;
import joptsimple.OptionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;

@Slf4j
public class JUnitFormatter implements Formatter {
    public static final String REPORT_FILE = "report-file";

    public static final String JUNIT = "junit";

    private final OptionParser parser = new OptionParser() {
        {
            acceptsAll(singletonList(REPORT_FILE), "File to store the test reports.")
                .withRequiredArg()
                .describedAs("Report file")
                .defaultsTo("report.xml");
            allowsUnrecognizedOptions();
        }
    };
    private String outputFile;

    @Override
    public boolean setup(String[] arguments) {
        outputFile = (String) parser.parse(arguments).valueOf(REPORT_FILE);
        return true;
    }

    /**
     * {@inheritDoc}
     * Writes out the report for the given testSuites in the JUnit XML format.
     */
    @Override
    public void writeReport(List<TestSuite> testSuites) throws IOException {
        Document document = DocumentHelper.createDocument();

        Element testSuitesRoot = document.addElement("testsuites");

        // Output for each test suite
        for (TestSuite testSuite : testSuites) {
            Element testSuiteRoot = testSuitesRoot.addElement("testsuite")
                                     .addAttribute("tests", Integer.toString(testSuite.queries.size() + testSuite.tests.size()))
                                     .addAttribute("name", testSuite.name);

            for (Query query : testSuite.queries) {
                Element queryNode = testSuiteRoot.addElement("testcase")
                                     .addAttribute("name", query.name);
                if (query.failed()) {
                    String failureMessage = StringUtils.join(query.getMessages(), ", ");
                    queryNode.addElement("failed").addText(failureMessage);
                }
            }
            for (Test test : testSuite.tests) {
                Element testNode = testSuiteRoot.addElement("testcase").addAttribute("name", test.name);
                if (test.failed()) {
                    String failedAsserts = StringUtils.join(test.getMessages(), ", ");
                    String failureMessage = "Description: " + test.description + ";\n" +
                                             "Failed asserts: " + failedAsserts + "\n";
                    testNode.addElement("failed").addText(failureMessage);
                }
            }
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileWriter(outputFile), format);
        writer.write(document);
        writer.close();
    }

    @Override
    public void printHelp() {
        Helpable.printHelp("Junit report options", parser);
    }

    @Override
    public String getName() {
        return JUNIT;
    }

}
