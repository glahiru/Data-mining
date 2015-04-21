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
package com.datamining.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommonUtils {
    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static void mergeFiles(File[] files, File mergedFile) {

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, false);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            System.out.println("merging: " + f.getName());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method split the dataset in to foldSize number of files
     * @param treeFileName     tree file name this file should be in the classpath
     * @param foldSize         number of folds
     * @return
     */
    public static List<File> splitData(String treeFileName, int foldSize) {
        ArrayList<File> tempFilePaths = new ArrayList<File>();
        Random random = new Random(100);
        for (int i = 0; i < foldSize+1; i++) {
            tempFilePaths.add(new File(String.valueOf(random.nextInt())));
        }
        URL treeResource = CommonUtils.class.getClassLoader().getResource(treeFileName);
        if (treeResource == null) {
            logger.error("Make sure the file is in the classpath of the project: " + treeFileName);
            return null;
        }

        URL trainResource = null;

        File treeFile = new File(treeResource.getPath());
        ArrayList<String> tempAttributeList = new ArrayList<String>();
        try {
            FileReader treeFileInputStream = new FileReader(treeFile);
            BufferedReader bufferedReader = new BufferedReader(treeFileInputStream);
            String line = null;
            String line1 = null;
            int count = 0;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if (count == 0) {
                        trainResource = CommonUtils.class.getClassLoader().getResource(line);
                        if (trainResource == null) {
                            logger.error("Make sure the file is in the classpath of the project: " + line);
                            return null;
                        }
                        FileReader fileReader = new FileReader(new File(trainResource.getPath()));
                        BufferedReader bufferedReader1 = new BufferedReader(fileReader);
                        int count1 = 0;
                        while ((line1 = bufferedReader1.readLine()) != null) {
                            if (count1 == 0) {
                                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFilePaths.get(0)));
                                bufferedWriter.write(line1);
                                bufferedWriter.write("\n");
                                bufferedWriter.flush();
                                bufferedWriter.close();
                            }else {
                                tempAttributeList.add(line1);
                            }
                            count1++;
                        }
                    }
                    count++;
                    logger.info(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }


        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        // now we randomly write data
        int fileIndex = 1;
        while(tempAttributeList.size()!=0) {
            int index0 = new Random(100).nextInt();
            if(index0<0){
                index0 = index0 * -1;
            }
            String s = tempAttributeList.get(index0 % tempAttributeList.size());

            try {
                BufferedWriter bufferedWriter1 = new BufferedWriter(new FileWriter(tempFilePaths.get(fileIndex),true));
                bufferedWriter1.write(s);
                bufferedWriter1.write("\n");
                bufferedWriter1.flush();
                bufferedWriter1.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            tempAttributeList.remove(s);
            if(fileIndex==foldSize){
                fileIndex=1;
            }else{
                fileIndex++;
            }
        }
        return tempFilePaths;
    }
}
