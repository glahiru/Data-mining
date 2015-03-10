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

public class Attribute {
    private final static Logger logger = LoggerFactory.getLogger(Attribute.class);

    private String name;

    private Map<String,AttributeValue> values;

    private int tupleIndex;

    private double entrypy;

    public Attribute(String name, Map<String,AttributeValue> values, int tupleIndex) {
        this.name = name;
        this.values = values;
        this.tupleIndex = tupleIndex;
    }

    public Attribute(String name, int tupleIndex) {
        this.name = name;
        this.values = new HashMap<String, AttributeValue>();
        this.tupleIndex = tupleIndex;
    }

    public int getSize() {
        return values.size();
    }

    public void addValue(String value) {
        values.put(value, new AttributeValue(value));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String,AttributeValue> getValues() {
        return values;
    }

    public void setValues(Map<String,AttributeValue> values) {
        this.values = values;
    }

    public int getTupleIndex() {
        return tupleIndex;
    }

    public void setTupleIndex(int tupleIndex) {
        this.tupleIndex = tupleIndex;
    }

    public void calculateEntrypy() {
        int total = 0;
        Set<String> strings = values.keySet();
        for(String key:strings) {
            total+=values.get(key).getFrequency();
        }
        entrypy = 0;
        for(String key:strings) {
            double p = (double) (values.get(key).getFrequency()) / total;
            logger.info("Probability:" +key+":"+p);
            entrypy = entrypy - p * (Math.log(p) / Math.log(2));
        }

        logger.info("Entropy calculated: " + name + ": "+entrypy);
        logger.info("--------------------------------------------------------------------------------");

    }

    public double getEntrypy() {
        return entrypy;
    }

    public void setEntrypy(double entrypy) {
        this.entrypy = entrypy;
    }

    public void cleanUp(){
        Set<String> strings = values.keySet();
        for (String key : strings) {
            values.get(key).setFrequency(0);
        }
        entrypy=0;
    }
}
