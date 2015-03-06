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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ID3tree {
    private final static Logger logger = LoggerFactory.getLogger(ID3tree.class);

    private String  className;

    private Attribute classAttribute;

    private List<String> featureNames;

    private List<Attribute> featureAttributes;

    private DataSet dataSet;

    public DataSet loadData(String[] args) {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        ArrayList<TupleInstance> tupleInstances = new ArrayList<TupleInstance>();
        dataSet = new DataSet();
        if (args.length != 2) {
            logger.error("You should give the treeFile and testFilePath as inputs to run this program");
            return null;
        }
        String treeFilePath = args[0];
        String testFilePath = args[1];


        URL treeResource = ID3tree.class.getClassLoader().getResource(treeFilePath);
        if (treeResource == null) {
            logger.error("Make sure the file is in the classpath of the project: " + treeFilePath);
            return null;
        }
        URL testResource = ID3tree.class.getClassLoader().getResource(testFilePath);
        if (testResource == null) {
            logger.error("Make sure the file is in the classpath of the project: " + testFilePath);
            return null;
        }

        URL trainResource = null;

        File treeFile = new File(treeResource.getPath());
        File testFile = new File(testResource.getPath());
        logger.info("--------------------------------------------------------");

        try {
            FileReader treeFileInputStream = new FileReader(treeFile);
            BufferedReader bufferedReader = new BufferedReader(treeFileInputStream);
            String line = null;
            String line1 = null;
            int count = 0;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    if (count == 0) {
                        trainResource = ID3tree.class.getClassLoader().getResource(line);
                        if (trainResource == null) {
                            logger.error("Make sure the file is in the classpath of the project: " + line);
                        }
                        FileReader fileReader = new FileReader(new File(trainResource.getPath()));
                        BufferedReader bufferedReader1 = new BufferedReader(fileReader);
                        int count1 = 0;
                        while ((line1 = bufferedReader1.readLine()) != null) {
                            if (count1 == 0) {
                                String[] split = line1.split("\t");
                                for (int i = 0; i < split.length; i++) {
                                    Attribute attribute = new Attribute(split[i], i);
                                    attributes.add(i,attribute);
                                }
                            }else {
                                tupleInstances.add(new TupleInstance(Arrays.asList(line1.split("\t"))));
                                String[] split = line1.split("\t");
                                for (int i = 0; i < split.length ; i++) {
                                    attributes.get(i).addValue(split[i]);
                                }
                            }
                            count1++;
                        }
                    } else if (count == 1) {
                        className = line;
                    } else if (count == 2) {
                        String[] split = line.split("\t");
                        featureNames = Arrays.asList(split);
                    }
                    count++;
                    logger.info(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            logger.info("--------------------------------------------------------");
            treeFileInputStream = new FileReader(testFile);
            bufferedReader = new BufferedReader(treeFileInputStream);
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    logger.info(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("--------------------------------------------------------");



        dataSet.setDataPoint(tupleInstances);
        dataSet.setDefinition(new DataSetDefinition(attributes));
        dataSet.setClassIndex(dataSet.getDefinition().getIndex(className));
        return dataSet;
    }




    /**
     * After loading the dataset now we are building the classification Tree
     */
    public void buildClassifier(DataSet dataSet,Node node){
        // after loading
        if(node.isLeafNode()){
            return;
        }
        while(!(dataSet.isEmpty() || dataSet.allInOneClass() || dataSet.isFeaturesEmpty())){ // we check thred conditions


        }
    }

    public void printDataSet() {
        logger.info("---------------------  Printing Attribute List ---------------------\n\n");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < dataSet.getDefinition().getAttributes().size(); i++) {
            Attribute attribute = dataSet.getDefinition().getAttributes().get(i);
            stringBuffer.append("Attribute Name: ");
            stringBuffer.append(attribute.getName());
            stringBuffer.append("   Values: {");
            for (int j = 0; j < attribute.getValues().size(); j++) {
                stringBuffer.append(attribute.getValues().get(j).getValue());
                stringBuffer.append("   ");
            }
            stringBuffer.append("}\n");
            logger.info(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());
        }

        List<TupleInstance> tupleInstances = dataSet.getDataPoint();
        logger.info("--------------------------Training DataSet---------------------------");

        for (int i = 0; i < tupleInstances.size(); i++) {
            stringBuffer.append("{");
            for (int j = 0; j < tupleInstances.get(i).values.size(); j++) {
                stringBuffer.append(tupleInstances.get(i).values.get(j));
                stringBuffer.append("   ");
            }
            stringBuffer.append("}");
            logger.info(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());
        }

        logger.info("--------------------------Class Details---------------------------");


        Attribute attributeByName = dataSet.getDefinition().getAttributeByName(className);
        for (int i = 0; i < attributeByName.getValues().size(); i++) {
            stringBuffer.append(attributeByName.getValues().get(i));
            stringBuffer.append("   ");
        }

        printAttribute(attributeByName);

        featureAttributes = new ArrayList<Attribute>();
        logger.info("--------------------------Feature Details---------------------------");

        for (int i = 0; i < featureNames.size(); i++) {
            Attribute attributeByName1 = dataSet.getDefinition().getAttributeByName(featureNames.get(i));
            featureAttributes.add(attributeByName1);
            printAttribute(attributeByName1);
        }
    }

    private void printAttribute(Attribute attribute) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Name: ").append(attribute.getName());
        stringBuffer.append("   Values: {");
        for (int i = 0; i < attribute.getValues().size(); i++) {
            stringBuffer.append(attribute.getValues().get(i).getValue());
            stringBuffer.append("   ");
        }
        stringBuffer.append("   }");
        logger.info(stringBuffer.toString());
    }
}
