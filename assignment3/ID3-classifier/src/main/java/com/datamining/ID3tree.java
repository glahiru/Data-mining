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
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class ID3tree {
    private final static Logger logger = LoggerFactory.getLogger(ID3tree.class);

    private String  className;

    private Attribute classAttribute;

    private List<String> featureNames;

    private Map<String,Attribute> featureAttributes;

    private DataSet dataSet;

    private Node rootNode;


    private String mostFrequentClassValue;          // when a particular leaf node doesn't have any value we set this classValue



    public ID3tree() {
        rootNode = new Node();
        featureAttributes = new HashMap<String,Attribute>();
    }

    public DataSet loadData(String[] args) {
        Map<String,Attribute> attributes = new HashMap<String,Attribute>();
        ArrayList<TupleInstance> tupleInstances = new ArrayList<TupleInstance>();
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
        ArrayList<Attribute> tempAttributeLisy = new ArrayList<Attribute>();
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
                                    attributes.put(split[i], attribute);
                                    tempAttributeLisy.add(attribute);
                                }
                            }else {

                                String[] split = line1.split("\t");
                                HashMap<String, String> tupleData = new HashMap<String, String>();

                                for (int i = 0; i < split.length ; i++) {
                                    tupleData.put(tempAttributeLisy.get(i).getName(), split[i]);
                                }
                                for (int i = 0; i < split.length ; i++) {
                                    attributes.get(tempAttributeLisy.get(i).getName()).addValue(split[i]);
                                }
                                tupleInstances.add(new TupleInstance(tupleData));
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



        for(String feather:featureNames) {
            featureAttributes.put(feather, attributes.get(feather));
        }
        dataSet = new DataSet(className);
        dataSet.setDefinition(new DataSetDefinition(attributes));
        dataSet.setFeatureList(featureAttributes);
        for(TupleInstance instance:tupleInstances){
            dataSet.addDataPoint(instance);
        }
        rootNode.setDataSet(dataSet);
        return dataSet;
    }

    void setMostFrequentClassValue() {
        // after loading the dataset we have to calculate this
        mostFrequentClassValue = getMostFrequentClassValue(this.dataSet);
    }




    /**
     * After loading the dataset now we are building the classification Tree
     */
    public void buildClassifier(DataSet dataSet,Node node) {
        // after loading
        if (!(dataSet.isEmpty() || dataSet.allInOneClass() || dataSet.isFeaturesEmpty())) { // we check three conditions
            dataSet.calculateEntropies();
            Attribute minEntropyAttribute = dataSet.getMinEntropyAttribute();
            node.setAttribute(minEntropyAttribute);

            Map<String, DataSet> dataSets = dataSet.splitByAttribute(dataSet, minEntropyAttribute);
            Iterator<String> iterator = dataSets.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                DataSet dataSet1 = dataSets.get(next);
                Node node1 = new Node(minEntropyAttribute,dataSet1,node);
                node.addChild(next,node1);
                buildClassifier(dataSet1, node1);
            }
        }

        if (dataSet.allInOneClass() && !dataSet.isEmpty()) {
            node.setFinalClassName(dataSet.getClassifiedClassName()); // we know allInOnceClass so we take that class
        } else if (dataSet.isEmpty()) {
            node.setFinalClassName(this.getMostFrequentClassValue(this.getDataSet()));
        } else if (dataSet.isFeaturesEmpty()) {
            // here we have to calculate the final classname using the dataset
            node.setFinalClassName(getMostFrequentClassValue(dataSet));
        }
    }

    private String getMostFrequentClassValue(DataSet dataSet) {
        Attribute classAttribute = dataSet.getDefinition().getAttributes().get(dataSet.getClassName());
        // take the first one as most frequent attribute
        AttributeValue mostFrequestAttribute = classAttribute.getValues().entrySet().iterator().next().getValue();
        Iterator<Map.Entry<String, AttributeValue>> iterator = classAttribute.getValues().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, AttributeValue> next = iterator.next();
            if(mostFrequestAttribute.getFrequency()<next.getValue().getFrequency()){
                mostFrequestAttribute = next.getValue();
            }
        }
        return mostFrequestAttribute.getValue();
    }

    public void printDataSet() {
        logger.info("---------------------  Printing Attribute List ---------------------\n\n");
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<Map.Entry<String, Attribute>> iterator = dataSet.getDefinition().getAttributes().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Attribute> next = iterator.next();
            Attribute attribute = next.getValue();
            stringBuffer.append("Attribute Name: ");
            stringBuffer.append(attribute.getName());
            stringBuffer.append("   Values: {");
            Iterator<Map.Entry<String, AttributeValue>> iterator1 = attribute.getValues().entrySet().iterator();
            while(iterator1.hasNext()){
                Map.Entry<String, AttributeValue> next1 = iterator1.next();
                stringBuffer.append(next1.getValue().getValue());
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
            Iterator<Map.Entry<String, String>> iterator1 = tupleInstances.get(i).getValues().entrySet().iterator();
            while(iterator1.hasNext()){
                Map.Entry<String, String> next = iterator1.next();
                stringBuffer.append(next.getValue());
                stringBuffer.append("   ");
            }
            stringBuffer.append("}");
            logger.info(stringBuffer.toString());
            stringBuffer.delete(0, stringBuffer.length());
        }

        logger.info("--------------------------Class Details---------------------------");


        Attribute attributeByName = dataSet.getDefinition().getAttributes().get(className);
        Iterator<Map.Entry<String, AttributeValue>> iterator1 = attributeByName.getValues().entrySet().iterator();
        while(iterator1.hasNext()){
            Map.Entry<String, AttributeValue> next = iterator1.next();
            AttributeValue value = next.getValue();
            stringBuffer.append(value.getValue());
            stringBuffer.append("   ");
        }

        printAttribute(attributeByName);


        logger.info("--------------------------Feature Details---------------------------");

        for (String feather:featureNames) {
            printAttribute(featureAttributes.get(feather));
        }
    }

    private void printAttribute(Attribute attribute) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Name: ").append(attribute.getName());
        stringBuffer.append("   Values: {");
        Iterator<Map.Entry<String, AttributeValue>> iterator = attribute.getValues().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, AttributeValue> next = iterator.next();
            stringBuffer.append(next.getValue().getValue());
            stringBuffer.append("   ");
        }
        stringBuffer.append("   }");
        logger.info(stringBuffer.toString());
    }

    public void printID3Tree() {
        LinkedBlockingDeque<Node> queue = new LinkedBlockingDeque<Node>();
        queue.push(rootNode);
        while(!queue.isEmpty()){
            Node node = queue.remove();
            if (!node.isLeafNode()) {
                logger.info(node.getAttribute().getName());
            } else {
                logger.info("leaf Node: " + node.getAttribute().getName() +" attribute Value:"+node.getBaseValue());
                logger.info("Classified Class:" + node.getFinalClassName());

            }

            Iterator<Map.Entry<String, Node>> iterator = node.getChildren().entrySet().iterator();
            while(iterator.hasNext()){
                queue.push(iterator.next().getValue());
            }

        }
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Attribute getClassAttribute() {
        return classAttribute;
    }

    public void setClassAttribute(Attribute classAttribute) {
        this.classAttribute = classAttribute;
    }

    public List<String> getFeatureNames() {
        return featureNames;
    }

    public void setFeatureNames(List<String> featureNames) {
        this.featureNames = featureNames;
    }

    public Map<String, Attribute> getFeatureAttributes() {
        return featureAttributes;
    }

    public void setFeatureAttributes(Map<String, Attribute> featureAttributes) {
        this.featureAttributes = featureAttributes;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }
}
