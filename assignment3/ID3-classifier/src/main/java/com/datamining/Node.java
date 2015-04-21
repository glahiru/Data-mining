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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private final static Logger logger = LoggerFactory.getLogger(Node.class);

    private Attribute attribute;

    private Map<String,Node> children;

    private String finalClassName;

    private DataSet dataSet;

    private Node parentNode;

    private String baseValue;


    public Node() {
        this.children = new HashMap<String, Node>(); // empty children
        this.parentNode = null;
    }

    public Node(Attribute attribute, DataSet dataSet, Node parentNode) {
        this.attribute = attribute;
        this.children = new HashMap<String, Node>();  // empty children
        this.dataSet = dataSet;
        this.parentNode = parentNode;
    }

    public boolean isLeafNode() {
        if(children.size()==0){
            return true;
        }
        return false;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Map<String,Node> getChildren() {
        return children;
    }

    public void setChildren(Map<String,Node> children) {
        this.children = children;
    }

    public void addChild(String value, Node node) {
        node.setParentNode(this); //
        node.setBaseValue(value);
        children.put(value, node);
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public String getFinalClassName() {
        return finalClassName;
    }

    public void setFinalClassName(String finalClassName) {
        this.finalClassName = finalClassName;
    }

    public String getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(String baseValue) {
        this.baseValue = baseValue;
    }
}
