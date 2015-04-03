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

public class DataSetDefinition {
    private final static Logger logger = LoggerFactory.getLogger(DataSetDefinition.class);

    Map<String,Attribute> attributes;


    public DataSetDefinition(DataSetDefinition dataSetDefinition) {
        Iterator<Map.Entry<String, Attribute>> iterator = dataSetDefinition.getAttributes().entrySet().iterator();
        Map<String, Attribute> attributes = new HashMap<String, Attribute>();
        while (iterator.hasNext()) {
            Map.Entry<String, Attribute> next = iterator.next();
            attributes.put(next.getKey(),new Attribute(next.getValue()));
        }
        this.attributes = attributes;
    }

    public DataSetDefinition(Map<String,Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String,Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String,Attribute> attributes) {
        this.attributes = attributes;
    }

    public Attribute getAttributeByName(String name) {
        return attributes.get(name);
    }

    public void cleanupCounts(){
        Iterator<Map.Entry<String, Attribute>> iterator = attributes.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Attribute> next = iterator.next();
            next.getValue().cleanUp();
        }
    }
}
