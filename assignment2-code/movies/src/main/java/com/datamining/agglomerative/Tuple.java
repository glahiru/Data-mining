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
import java.util.List;
import java.util.UUID;

/**
 * We need to store these clusters so we implement Serializable
 */
public class Tuple implements Serializable{
    private final static Logger logger = LoggerFactory.getLogger(Tuple.class);

    private List<Object> values;

    private String id;

    public Tuple(List<Object> values) {
        this.values = values;
        this.id = UUID.randomUUID().toString();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    Long _processSampleStartTime = null;
    Long _executeSampleStartTime = null;

    public void setProcessSampleStartTime(long ms) {
        _processSampleStartTime = ms;
    }

    public Long getProcessSampleStartTime() {
        return _processSampleStartTime;
    }

    public void setExecuteSampleStartTime(long ms) {
        _executeSampleStartTime = ms;
    }

    public Long getExecuteSampleStartTime() {
        return _executeSampleStartTime;
    }

    long _outAckVal = 0;

    public void updateAckVal(long val) {
        _outAckVal = _outAckVal ^ val;
    }

    public long getAckVal() {
        return _outAckVal;
    }

    public int size() {
        return values.size();
    }

    public Object getValue(int i) {
        return values.get(i);
    }

    public String getString(int i) {
        return (String) values.get(i);
    }

    public Integer getInteger(int i) {
        return (Integer) values.get(i);
    }

    public Long getLong(int i) {
        return (Long) values.get(i);
    }

    public Boolean getBoolean(int i) {
        return (Boolean) values.get(i);
    }

    public Short getShort(int i) {
        return (Short) values.get(i);
    }

    public Byte getByte(int i) {
        return (Byte) values.get(i);
    }

    public Double getDouble(int i) {
        return (Double) values.get(i);
    }

    public Float getFloat(int i) {
        return (Float) values.get(i);
    }

    public byte[] getBinary(int i) {
        return (byte[]) values.get(i);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /* Indexed */
    public Object nth(int i) {
        if(i < values.size()) {
            return values.get(i);
        } else {
            return null;
        }
    }

    public Object nth(int i, Object notfound) {
        Object ret = nth(i);
        if(ret==null) ret = notfound;
        return ret;
    }

    /* Counted */
    public int count() {
        return values.size();
    }

}
