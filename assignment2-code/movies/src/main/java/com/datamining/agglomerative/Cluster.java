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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * We need to store these clusters so we implement Serializable
 */
public class Cluster implements Serializable{
    private final static Logger logger = LoggerFactory.getLogger(Cluster.class);
    public static final String NOT_SET = "not-set";

    private int id;

    private int parentId;

    private Tuple data;

    private List<Integer> children;

    private boolean leafNode = false;
    public Cluster(int id) {
        this.id = id;
        this.parentId = -1;
        this.children = new ArrayList<Integer>();
    }

    public Cluster(int id,Tuple  dataObject) {
        this.id = id;
        this.data = dataObject;
        this.parentId = -1;
        this.leafNode = true;
        this.children = new ArrayList<Integer>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tuple getData() {
        return data;
    }

    public void setData(Tuple data) {
        this.data = data;
    }

    public List<Integer> getChildren() {
        return children;
    }

    public void setChildren(List<Integer> children) {
        this.children = children;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void addChild(int childId) {
        this.children.add(childId);
    }

    public boolean isLeafNode() {
        return leafNode;
    }

    public void setLeafNode(boolean leafNode) {
        this.leafNode = leafNode;
    }
}
