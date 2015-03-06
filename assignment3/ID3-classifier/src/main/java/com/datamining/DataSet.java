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

import java.util.ArrayList;
import java.util.List;

public class DataSet {
    private final static Logger logger = LoggerFactory.getLogger(DataSet.class);

    int classIndex;

    DataSetDefinition definition;

    List<TupleInstance> dataPoint;

    List<Attribute> featureList;

    public DataSet() {
    }

    public DataSet(DataSetDefinition definition, List<TupleInstance> dataPoint) {
        this.definition = definition;
        this.dataPoint = dataPoint;
    }

    public void addDataPoint(TupleInstance instance){
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

    public void setDataPoint(List<TupleInstance> dataPoint) {
        this.dataPoint = dataPoint;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public List<Attribute> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Attribute> featureList) {
        this.featureList = featureList;
    }


    public boolean allInOneClass() {
        String firstClassValue = dataPoint.get(0).getValues().get(classIndex);
        for (int i = 0; i < dataPoint.size(); i++) {
            if(!dataPoint.get(i).getValues().get(classIndex).equals(firstClassValue)){
                return false;
            }
        }
        return true;
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

    public List<Attribute> removeFeature(String name) {
        int index = -1;
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.get(i).getName().equals(name)) {
                index = i;
            }
        }
        if(index != -1) {
            featureList.remove(index);
        }
        return featureList;
    }

    List<DataSet> splitByAttribute(DataSet dataSet,Attribute attribute) {
        int attributeIndex = dataSet.getDefinition().getIndex(attribute.getName());
        ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        for (int i = 0; i < attribute.getValues().size(); i++) {
            DataSet dataSet1 = new DataSet(dataSet.getDefinition(), null);
            dataSet1.setFeatureList(dataSet.removeFeature(attribute.getName())); // we remove the selected attribute from feature list
            dataSets.add(new DataSet(dataSet.getDefinition(), null));
        }
        for (int i = 0; i < dataSet.getDataPoint().size(); i++) {
            TupleInstance tupleInstance = dataSet.getDataPoint().get(i);
            String s = tupleInstance.getValues().get(attributeIndex);
            Attribute attributeByName = dataSet.getDefinition().getAttributeByName(s);
            if(attributeByName==null){
                logger.error("Wrong input is given in training dataset");
                // in this case we drop this tuple because the value in this feature cannot be used
            }else {
                int i1 = dataSet.getDefinition().getAttributes().indexOf(attributeByName);
                dataSets.get(i1).addDataPoint(tupleInstance);
            }

        }
        return dataSets;
    }

    public void calculateEntropies() {
        for (int i = 0; i < featureList.size(); i++) {
            getFeatureList().get(i).calculateEntrypy();
        }
    }

    public Attribute getMinEntropyAttribute() {
        Attribute minEntropyAttr = getFeatureList().get(0);
        for (int i = 0; i < featureList.size(); i++) {
            Attribute attribute = getFeatureList().get(i);
            if(minEntropyAttr.getEntrypy()>attribute.getEntrypy()){
                minEntropyAttr = attribute;
            }
        }
        return minEntropyAttr;
    }
}


