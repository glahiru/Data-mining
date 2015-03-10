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

public class AttributeValue {
    private final static Logger logger = LoggerFactory.getLogger(AttributeValue.class);

    private String value;

    private int frequency;

    private double probabilities;               // this is useful when this is used in LeafNodes to hold probability of each attribute value

    public AttributeValue(String value) {
        this.value = value;
        this.frequency = 0;
        probabilities = 0;
    }

    public AttributeValue(String value, int frequency) {
        this.value = value;
        this.frequency = frequency;
    }

    public AttributeValue(AttributeValue attributeValue) {
        this.setProbabilities(attributeValue.getProbabilities());
        this.setValue(attributeValue.getValue());
        this.setFrequency(attributeValue.getFrequency());
    }

    public void incrementFrequence() {
        this.frequency++;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(double probabilities) {
        this.probabilities = probabilities;
    }


}
