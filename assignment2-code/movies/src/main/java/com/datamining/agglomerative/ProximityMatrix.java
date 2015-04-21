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
package com.datamining.agglomerative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProximityMatrix {
    private final static Logger logger = LoggerFactory.getLogger(ProximityMatrix.class);

    private int dataObjectSize;

    private Double[][] proximityMatrix;

    private List<EuclValue> euclValues;

    private EuclValue[] euclValuesArray;
    public ProximityMatrix(int dataObjectSize) {
        this.dataObjectSize = dataObjectSize;
        euclValues = new ArrayList<EuclValue>();
        euclValuesArray = new EuclValue[dataObjectSize * dataObjectSize * 2];
    }

    public int getDataObjectSize() {
        return dataObjectSize;
    }

    public void setDataObjectSize(int dataObjectSize) {
        this.dataObjectSize = dataObjectSize;
    }

    public Double[][] getProximityMatrix() {
        return proximityMatrix;
    }

    public void setProximityMatrix(Double[][] proximityMatrix) {
        this.proximityMatrix = proximityMatrix;
    }

    public List<EuclValue> getEuclValues() {
        return euclValues;
    }

    public void setEuclValues(List<EuclValue> euclValues) {
        this.euclValues = euclValues;
    }

    public EuclValue[] getEuclValuesArray() {
        return euclValuesArray;
    }

    public void setEuclValuesArray(EuclValue[] euclValuesArray) {
        this.euclValuesArray = euclValuesArray;
    }
}
