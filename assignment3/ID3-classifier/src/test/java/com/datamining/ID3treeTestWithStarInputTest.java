/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package com.datamining;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public class ID3treeTestWithStarInputTest {
    private final static Logger logger = LoggerFactory.getLogger(ID3treeTestWithStarInputTest.class);

    /**
     * Class is C
     */
    @Test
    public void test() throws FileNotFoundException {
        String[] args = new String[2];
        args[0] = "tree.txt";
        args[1] = "test2.txt";


        ID3tree id3tree = new ID3tree();
        id3tree.loadData(args,true);
        id3tree.printDataSet();
        id3tree.buildClassifier(id3tree.getDataSet(), id3tree.getRootNode());
        id3tree.printID3Tree();
        id3tree.runAllTests();
    }
}
