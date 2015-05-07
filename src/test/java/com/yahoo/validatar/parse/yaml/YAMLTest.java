/*
 * Copyright 2014-2015 Yahoo! Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yahoo.validatar.parse.yaml;

import com.yahoo.validatar.common.Query;
import com.yahoo.validatar.common.TestSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.Test;

public class YAMLTest {
    private YAML yaml = new YAML();

    @Test
    public void testLoadOfValidTestFile() throws FileNotFoundException {
        TestSuite testSuite = yaml.parse(new FileInputStream(new File("src/test/resources/sample-tests/tests.yaml")));
        Assert.assertEquals(testSuite.name, "Validatar Example");
    }

    @Test
    public void testParamNotReplaced() throws FileNotFoundException {
        TestSuite testSuite = yaml.parse(new FileInputStream(new File("src/test/resources/sample-tests/tests.yaml")));
        Query query = testSuite.queries.get(2);
        Assert.assertEquals(query.value, "TEST ${DATE}");
    }
}