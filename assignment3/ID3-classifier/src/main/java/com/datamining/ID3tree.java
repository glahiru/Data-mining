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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class ID3tree {
    private final static Logger logger = LoggerFactory.getLogger(ID3tree.class);

    private String  className;

    private Attribute classAttribute;

    private List<String> featureNames;

    private List<Integer> featureIndexes;

    private Map<String,Attribute> featureAttributes;

    private DataSet dataSet;

    private Node rootNode;

    private int classIndex;

    private String testFileName;

    private String treeFileName;

    private File testFile;

    private File treeFile;

    private File tableFile;

    private String mostFrequentClassValue;          // when a particular leaf node doesn't have any value we set this classValue

    List<TupleInstance> testData;

    public ID3tree() {
        rootNode = new Node();
        featureAttributes = new HashMap<String, Attribute>();
        testData = new ArrayList<TupleInstance>();
        featureIndexes = new ArrayList<Integer>();
    }


    public static void main(String[] args) throws Exception {
        ID3tree id3tree = new ID3tree();
        try {
            id3tree.loadData(args,true);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            System.out.println("Error loading data from the given files");
            throw new Exception("Error loading data from the given files");
        }
        id3tree.printDataSet();
        id3tree.buildClassifier(id3tree.getDataSet(), id3tree.getRootNode());
        id3tree.printID3Tree();
        id3tree.runAllTests();
    }


    /**
     * This is the very first method to invoke to build the classifier
     */
    public DataSet loadData(String[] args, boolean loadTestData) throws MalformedURLException {
        Map<String, Attribute> attributes = new HashMap<String, Attribute>();
        ArrayList<TupleInstance> tupleInstances = new ArrayList<TupleInstance>();
        if (args.length != 2) {
            logger.error("You should give the treeFile and testFilePath as inputs to run this program");
            return null;
        }
        treeFileName = args[0];
        testFileName = args[1];
        new File(".").getAbsolutePath();

        URL treeResource = ID3tree.class.getClassLoader().getResource(treeFileName);

        if (treeResource == null || !new File(treeResource.getPath()).exists()) {
            treeResource = new File("conf"+File.separator+args[0]).toURI().toURL();
            treeFileName = treeResource.getFile();
        }

        URL testResource = ID3tree.class.getClassLoader().getResource(testFileName);


        if (testResource == null || !new File(testResource.getPath()).exists()) {
            testResource = new File("conf"+File.separator+args[0]).toURI().toURL();
            testFileName = testResource.getFile();
        }

        URL trainResource = null;

        treeFile = new File(treeResource.getPath());

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
                        if (trainResource == null || !new File(trainResource.getPath()).exists()) {
                            trainResource = new File("conf"+ File.separator + line).toURI().toURL();
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
                            } else {

                                String[] split = line1.split("\t");
                                HashMap<String, String> tupleData = new HashMap<String, String>();
                                // adding the train data list
                                for (int i = 0; i < split.length; i++) {
                                    tupleData.put(tempAttributeLisy.get(i).getName(), split[i]);
                                }

                                // updating the attribute frequencies while adding tuples
                                for (int i = 0; i < split.length; i++) {
                                    attributes.get(tempAttributeLisy.get(i).getName()).addValue(split[i]);
                                }
                                tupleInstances.add(new TupleInstance(tupleData, 0));
                            }
                            count1++;
                        }
                    } else if (count == 1) {
                        className = line;
                        int index = 0;
                        for (Attribute attr : tempAttributeLisy) {
                            if (attr.getName().equals(className)) {
                                classIndex = index;
                            }
                            index++;
                        }


                    } else if (count == 2) {
                        String[] split = line.split("\t");
                        featureNames = Arrays.asList(split);
                        for (int i = 0; i < featureNames.size(); i++) {
                            String featureName = featureNames.get(i);
                            int index = 0;
                            for (Attribute attr : tempAttributeLisy) {
                                if (attr.getName().equals(featureName)) {
                                    featureIndexes.add(i, index);
                                }
                                index++;
                            }
                        }
                    }
                    count++;
                    logger.info(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            for (String feather : featureNames) {
                featureAttributes.put(feather, attributes.get(feather));
            }
            dataSet = new DataSet(className);
            dataSet.setDefinition(new DataSetDefinition(attributes));
            dataSet.setFeatureList(featureAttributes);
            for (TupleInstance instance : tupleInstances) {
                dataSet.addDataPoint(instance);
            }

            loadTestData(args);

            rootNode.setDataSet(dataSet);

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }


        return dataSet;
    }

    public void loadTestData(String[] args) throws FileNotFoundException, MalformedURLException {
        URL testResource = ID3tree.class.getClassLoader().getResource(testFileName);

        if(testResource==null || !new File(testResource.getPath()).exists()) {
            testResource =new File("conf"+File.separator+args[1]).toURI().toURL();
            testFileName = testResource.getFile();
        }
        String line = null;
        testFile = new File(testResource.getPath());

        FileReader treeFileInputStream = new FileReader(testFile);
        BufferedReader bufferedReader = new BufferedReader(treeFileInputStream);
        int count=0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length != featureNames.size()) {
                    logger.error("Wrong input, all the feature values should be there in the tests");
                    logger.error("data:" + line + " will not be tested");
                } else {
                    HashMap<String, String> tupleData = new HashMap<String, String>();

                    for (int i = 0; i < split.length; i++) {
//                        if ("*".equals(split[i])) {
//                            logger.info("Empty intput came for testing so we set the most frequent attribute");
//                            tupleData.put(featureNames.get(i), getMostFrequentAttributeValue(dataSet, featureNames.get(i)));
//                        } else {
                            tupleData.put(featureNames.get(i), split[i]);
//                        }
                    }

                    testData.add(new TupleInstance(tupleData, count));
                }
                count++;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    public DataSet loadDataCV(File[] args, boolean loadTestData) {
        Map<String,Attribute> attributes = new HashMap<String,Attribute>();
        ArrayList<TupleInstance> tupleInstances = new ArrayList<TupleInstance>();
        if (args.length != 3) {
            logger.error("You should give the treeFile and testFilePath and trainingFile");
            return null;
        }

        File treeFile = args[0];
        File testFile = args[1];
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
                        FileReader fileReader = new FileReader(args[2]);
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
                                // adding the train data list
                                for (int i = 0; i < split.length ; i++) {
                                    tupleData.put(tempAttributeLisy.get(i).getName(), split[i]);
                                }

                                // updating the attribute frequencies while adding tuples
                                for (int i = 0; i < split.length ; i++) {
                                    attributes.get(tempAttributeLisy.get(i).getName()).addValue(split[i]);
                                }
                                tupleInstances.add(new TupleInstance(tupleData, 0));
                            }
                            count1++;
                        }
                    } else if (count == 1) {
                        className = line;
                        int index = 0;
                        for (Attribute attr : tempAttributeLisy) {
                            if (attr.getName().equals(className)) {
                                classIndex = index;
                            }
                            index++;
                        }
                    } else if (count == 2) {
                        String[] split = line.split("\t");
                        featureNames = Arrays.asList(split);
                        for (int i = 0; i < featureNames.size() ; i++) {
                            String featureName = featureNames.get(i);
                            int index = 0;
                            for(Attribute attr:tempAttributeLisy){
                                if(attr.getName().equals(featureName)){
                                    featureIndexes.add(i,index);
                                }
                                index++;
                            }
                        }
                    }
                    count++;
                    logger.info(line);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            if(loadTestData) {
                treeFileInputStream = new FileReader(testFile);
                bufferedReader = new BufferedReader(treeFileInputStream);

                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        logger.info(line);
                        String[] split = line.split("\t");
                        if (split.length != featureNames.size()) {
                            logger.error("Wrong input, all the feature values should be there in the tests");
                            logger.error("data:" + line + " will not be tested");
                        } else {
                            HashMap<String, String> tupleData = new HashMap<String, String>();

                            for (int i = 0; i < split.length; i++) {
                                tupleData.put(featureNames.get(i), split[i]);
                            }

                            testData.add(new TupleInstance(tupleData, 0));
                        }

                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }

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



    /**
     * This method should be invoked after loadData and buildClassifier operations
     * so the ID3 is ready to the the classification
     *
     * @param tupleInstance
     */
    public String runClassifier(TupleInstance tupleInstance) throws FileNotFoundException {
        System.out.println("\n");
        System.out.print("tuple:    ");


        FileReader treeFileInputStream = new FileReader(testFile);
        BufferedReader bufferedReader = new BufferedReader(treeFileInputStream);
        String line=null;
        int count=0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if(count==tupleInstance.getLineNumber()) {
                    System.out.print(line + "    ");
                }
                count++;

            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        System.out.println("\n");
        Node node = rootNode;
        while (!node.isLeafNode()) {
            String attributeValue = tupleInstance.getValues().get(node.getAttribute().getName());
            if("*".equals(attributeValue)) {
                System.out.println("test attribute is * for attribute:" + node.getAttribute().getName());
                attributeValue = getMostFrequentAttributeValue(node.getDataSet(), node.getAttribute().getName());
                System.out.println("So we set the node local highest probability attribute:" + attributeValue);
            }
            Node node1 = node.getChildren().get(attributeValue);
            if (node1 == null) {
                break;
            } else {
                node = node1;
            }
        }


        System.out.print("class:    ");

        Iterator<Map.Entry<String, AttributeValue>> iterator1 = dataSet.getDefinition().getAttributes().get(className).getValues().entrySet().iterator();
        boolean totalZero = false;
        while(iterator1.hasNext()){
            Map.Entry<String, AttributeValue> next = iterator1.next();
            double classProbabilityByName = node.getDataSet().getClassProbabilityByName(next.getValue().getValue());
            if (classProbabilityByName == -1) {
                totalZero=true;
                System.out.print(next.getKey() + ": 0.0" + "    ");
            } else {
                System.out.print(next.getKey() + ":" + node.getDataSet().getClassProbabilityByName(next.getValue().getValue()) + "    ");
            }
        }

        if(totalZero){
            System.out.print("most frequent class:" + getMostFrequentClassValue(dataSet));
        }
        System.out.println("\n");
        return getMostFrequentClassValue(node.getDataSet());

    }
    public String runClassifierCV(TupleInstance tupleInstance) throws FileNotFoundException {
        System.out.println("\n");
        System.out.print("tuple:    ");
        Iterator<Map.Entry<String, String>> iterator = tupleInstance.getValues().entrySet().iterator();
        while(iterator.hasNext()){
            String value = iterator.next().getValue();
            System.out.print(value+"    ");
        }
        Node node = rootNode;
        while (!node.isLeafNode()) {
            Node node1 = node.getChildren().get(tupleInstance.getValues().get(node.getAttribute().getName()));
            if(node1==null){
                break;
            }else{
                node = node1;
            }
        }
        System.out.print("\nclass:    ");

        Iterator<Map.Entry<String, AttributeValue>> iterator1 = dataSet.getDefinition().getAttributes().get(className).getValues().entrySet().iterator();
        boolean totalZero = false;
        while(iterator1.hasNext()){
            Map.Entry<String, AttributeValue> next = iterator1.next();
            double classProbabilityByName = node.getDataSet().getClassProbabilityByName(next.getValue().getValue());
            if (classProbabilityByName == -1) {
                totalZero=true;
                System.out.print(next.getKey() + ": 0.0" + "    ");
            } else {
                System.out.print(next.getKey() + ":" + node.getDataSet().getClassProbabilityByName(next.getValue().getValue()) + "    ");
            }
        }

        if(totalZero){
            System.out.print("most frequent class:" + getMostFrequentClassValue(dataSet));
        }
        return getMostFrequentClassValue(node.getDataSet());

    }

    public void runAllTests() throws FileNotFoundException {
        for(TupleInstance tupleInstance:testData){
            runClassifier(tupleInstance);
        }
    }

    public int runAllTestsCV(List<String> knownClasses) throws FileNotFoundException {
        int count = 0;
        for (int i = 0; i < testData.size(); i++) {
            String s = runClassifierCV(testData.get(i));
            if (s.equals(knownClasses.get(i))) {
                count++;
                System.out.println("Correct Results");
            }else {
                System.out.println("Incorrect Results");
            }
        }
        return count;
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
        return getMostFrequentAttributeValue(dataSet, dataSet.getClassName());
    }

    private String getMostFrequentAttributeValue(DataSet dataSet,String attributeName) {
        Attribute classAttribute = dataSet.getDefinition().getAttributes().get(attributeName);
        // take the first one as most frequent attribute
        AttributeValue mostFrequestAttribute = classAttribute.getValues().entrySet().iterator().next().getValue();
        Iterator<Map.Entry<String, AttributeValue>> iterator = classAttribute.getValues().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AttributeValue> next = iterator.next();
            if (mostFrequestAttribute.getFrequency() < next.getValue().getFrequency()) {
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



    public List<Integer> getFeatureIndexes() {
        return featureIndexes;
    }

    public void setFeatureIndexes(List<Integer> featureIndexes) {
        this.featureIndexes = featureIndexes;
    }

    public String getMostFrequentClassValue() {
        return mostFrequentClassValue;
    }

    public void setMostFrequentClassValue(String mostFrequentClassValue) {
        this.mostFrequentClassValue = mostFrequentClassValue;
    }

    public List<TupleInstance> getTestData() {
        return testData;
    }

    public void setTestData(List<TupleInstance> testData) {
        this.testData = testData;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }
}
