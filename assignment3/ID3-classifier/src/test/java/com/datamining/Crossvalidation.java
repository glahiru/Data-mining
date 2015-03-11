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

import com.datamining.utils.CommonUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crossvalidation {
    private final static Logger logger = LoggerFactory.getLogger(Crossvalidation.class);

    /*
        Cross validation size  this is configurable
     */
    public static final int FOLD_SIZE = 3;

    @Test
    public void runThreeFoldCrossValidation() {
        /* this method split the data in to three partitions (randomly)
            build three different trees and run tests data using remaining data
         */
        File headerFile = null;
        File tempTest = null;
        List<File> files = null;
        try {
            File[] args = new File[3];
            String treeFile = "tree.txt";
            List<String> testClassList = new ArrayList<String>();

            files = CommonUtils.splitData(treeFile, FOLD_SIZE);

            headerFile = files.get(0);
            files.remove(0); // we remove the header file so its easy iterate only between data files
            //

            args[0] = new File(ID3tree.class.getClassLoader().getResource("treeCV.txt").getPath());
            tempTest = new File("tableCV.txt");

            File updatedTestFile = new File("testCV.txt");

            for (int i = 0; i < files.size(); i++) {
                File testFile = files.get(i);
                args[1] = testFile;
                args[2] = tempTest;
                ArrayList<File> filesList = new ArrayList<File>(files);
                filesList.remove(i);

                File[] files2 = new File[filesList.size() + 1];
                for (int j = 0; j < filesList.size() + 1; j++) {
                    if (j == 0) {
                        files2[0] = headerFile;
                    } else {
                        files2[j] = filesList.get(j - 1);
                    }
                }
                CommonUtils.mergeFiles(files2, tempTest);
                logger.info("--------------------------------------------------------------------------------");
                logger.info("Running Cross Validation iteration: " + i);
                logger.info("--------------------------------------------------------------------------------");
                ID3tree id3tree = new ID3tree();
                ID3tree id3treeTmp = new ID3tree();

                id3treeTmp.loadDataCV(args, false);  // we use this to get

                List<Integer> featureIndexes = id3treeTmp.getFeatureIndexes();
                int classIndex = id3treeTmp.getClassIndex();
                FileReader fileInputStream = new FileReader(testFile);
                BufferedReader bufferedReader = new BufferedReader(fileInputStream);

                FileWriter fileWriter = new FileWriter(updatedTestFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                String line = null;
                while((line=bufferedReader.readLine())!=null) {
                    String[] split = line.split("\t");
                    int count = 0;

                    testClassList.add(split[classIndex]);

                    for(Integer index:featureIndexes) {
                        bufferedWriter.write(split[index.intValue()]);
                        if (!(count == featureIndexes.size() - 1)) {
                            bufferedWriter.write("\t");
                        } else {
                            bufferedWriter.write("\n");
                        }

                        count++;
                    }
                }
                bufferedWriter.flush();
                bufferedWriter.close();
                args[1]=updatedTestFile;

                id3tree.loadDataCV(args, true);
                id3tree.buildClassifier(id3tree.getDataSet(), id3tree.getRootNode());

                int i1 = id3tree.runAllTestsCV(testClassList);

                logger.info("--------------------------------------------------------------------------------");
                logger.info("Finished Cross Validation iteration: " + i);
                logger.info("Accuracy level: " + (double)i1/testClassList.size());
                logger.info("--------------------------------------------------------------------------------");

                tempTest.delete();
            }



            System.out.println(headerFile.getAbsolutePath());
        }catch (Exception e){

        }finally {
            headerFile.delete();
            tempTest.delete();
            for (File file : files) {
                file.delete();
            }
        }
//        id3tree.printDataSet();
//        id3tree.buildClassifier(id3tree.getDataSet(), id3tree.getRootNode());
//        id3tree.printID3Tree();
//        id3tree.runAllTests();
    }


}
