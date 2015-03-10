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

import java.util.*;

public class DataSet {
    private final static Logger logger = LoggerFactory.getLogger(DataSet.class);

    private String className;

    private DataSetDefinition definition;

    private List<TupleInstance> dataPoint;

    private Map<String,Attribute> featureList;

    private UUID id;

    public DataSet(String className) {
        this.dataPoint = new ArrayList<TupleInstance>();
        this.id = UUID.randomUUID();
        this.className = className;
    }

    public DataSet(String className,DataSetDefinition definition) {
        this.definition = definition;
        this.dataPoint = new ArrayList<TupleInstance>();
        this.id = UUID.randomUUID();
        this.className = className;
    }

    public void addDataPoint(TupleInstance instance) {
        // here frequencies should be updated
        Iterator<String> iterator = instance.getValues().keySet().iterator();
        while(iterator.hasNext()){
            String attributeName = iterator.next();
            String attributeValue = instance.getValues().get(attributeName);
            Attribute attribute = this.definition.getAttributes().get(attributeName);
            if(attribute!=null) {
                AttributeValue attributeValue1 = attribute.getValues().get(attributeValue);
                attributeValue1.incrementFrequence();
            }
        }
        dataPoint.add(instance);
    }


    public DataSetDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(DataSetDefinition definition) {
        this.definition = definition;
    }

    public List<TupleInstance> getDataPoint() {
        return dataPoint;
    }


    public Map<String,Attribute> getFeatureList() {
        return featureList;
    }


    public void setFeatureList(Map<String,Attribute> featureList) {
        this.featureList = featureList;
    }




    public boolean isEmpty() {
        if(dataPoint.size()==0){
            return true;
        }
        return false;
    }

    public boolean isFeaturesEmpty(){
        if(featureList.size()==0){
            return true;
        }
        return false;
    }

    public Map<String,Attribute> removeFeature(String name) {
        featureList.remove(name);
        return featureList;
    }

    Map<String,DataSet> splitByAttribute(DataSet dataSet,Attribute attribute) {
        Map<String,DataSet> dataSets = new HashMap<String, DataSet>();

        Iterator<Map.Entry<String, AttributeValue>> iterator = attribute.getValues().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, AttributeValue> next = iterator.next();
            DataSet dataSet1 = new DataSet(className, new DataSetDefinition(dataSet.getDefinition()));
            dataSet1.cleanUpCounts();
            Map<String, Attribute> featureList1 = new HashMap<String, Attribute>(dataSet.getFeatureList());
            featureList1.remove(attribute.getName());
            dataSet1.setFeatureList(featureList1); // we remove the selected attribute from feature list
            dataSets.put(next.getValue().getValue(), dataSet1);

        }

        for (int i = 0; i < dataSet.getDataPoint().size(); i++) {
            TupleInstance tupleInstance = dataSet.getDataPoint().get(i);
            String s = tupleInstance.getValues().get(attribute.getName());
            dataSets.get(s).addDataPoint(tupleInstance);
        }
        logger.info("Data set " + dataSet.getId() + " is split in to " + dataSets.size() + " subsets based on attribute: " + attribute.getName());
        return dataSets;
    }

    public void calculateEntropies() {
        Iterator<Map.Entry<String, Attribute>> iterator = featureList.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Attribute> next = iterator.next();
            next.getValue().calculateEntrypy();
        }
    }

    public Attribute getMinEntropyAttribute() {
        Attribute minEntropyAttr = featureList.get(featureList.keySet().iterator().next());
        Iterator<Map.Entry<String, Attribute>> iterator = featureList.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Attribute> next = iterator.next();
            next.getValue().calculateEntrypy();
            if(minEntropyAttr.getEntrypy()>next.getValue().getEntrypy()){
                minEntropyAttr = next.getValue();
            }
        }
        return minEntropyAttr;
    }

    public boolean allInOneClass() {
        if(dataPoint.size()>0) {
            String firstTupleClassValue = dataPoint.get(0).getValues().get(className);
            for (int i = 0; i < dataPoint.size(); i++) {
                if (!dataPoint.get(i).getValues().get(className).equals(firstTupleClassValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getClassifiedClassName() {
        if(allInOneClass()){
            return dataPoint.get(0).getValues().get(className);
        }else {
            return "n/a";
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void cleanUpCounts(){
        this.definition.cleanupCounts();
    }
}


