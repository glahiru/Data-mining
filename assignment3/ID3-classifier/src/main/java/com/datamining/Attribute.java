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

public class Attribute {
    private final static Logger logger = LoggerFactory.getLogger(Attribute.class);

    private String name;

    private List<AttributeValue> values;

    private int tupleIndex;

    private double entrypy;

    public Attribute(String name, List<AttributeValue> values, int tupleIndex) {
        this.name = name;
        this.values = values;
        this.tupleIndex = tupleIndex;
    }

    public Attribute(String name, int tupleIndex) {
        this.name = name;
        this.values = new ArrayList<AttributeValue>();
        this.tupleIndex = tupleIndex;
    }

    public int getIndex(String value) {
        return values.indexOf(value);
    }

    public int getSize() {
        return values.size();
    }

    public void addValue(String value) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getValue().equals(value)) {
                return;
            }
        }
        values.add(new AttributeValue(value));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AttributeValue> getValues() {
        return values;
    }

    public void setValues(List<AttributeValue> values) {
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
        for (int i = 0; i < values.size(); i++) {
            total+=values.get(i).frequency;
        }
        entrypy = 0;
        for (int i = 0; i < values.size(); i++) {
            double p = values.get(i).frequency / total;
            entrypy = entrypy - p * (Math.log(p)/Math.log(2));
        }
        logger.info("Entropy calculated: " + name + ": "+entrypy);

    }

    public double getEntrypy() {
        return entrypy;
    }

    public void setEntrypy(double entrypy) {
        this.entrypy = entrypy;
    }
}
