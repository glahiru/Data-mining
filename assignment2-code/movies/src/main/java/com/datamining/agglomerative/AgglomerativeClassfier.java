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

import com.datamining.agglomerative.utils.ClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgglomerativeClassfier implements Classifier {
    private final static Logger logger = LoggerFactory.getLogger(AgglomerativeClassfier.class);

    private ProximityMatrix proximityMatrix;

    private List<Cluster> clusterList;

    public AgglomerativeClassfier(int size) {
        this.proximityMatrix = new ProximityMatrix(size);
        clusterList = new ArrayList<Cluster>();
    }

    public void loadClassifier(Tuple data) {
        Cluster cluster = new Cluster(clusterList.size(), data);
        clusterList.add(cluster);
    }

    public void calcProximityMatrix() throws InterruptedException {
        for (int i = 0; i < clusterList.size(); i++) {
            Tuple clusterOut = clusterList.get(i).getData();
            for (int j = i + 1; j < clusterList.size(); j++) { // j=i is because we only calculate half of the matrix because of the symmetry
                Tuple clusterIn = clusterList.get(j).getData();
                double sqrt = Math.sqrt(Math.pow(clusterOut.getDouble(0) - clusterIn.getDouble(0), 2) +
                        Math.pow(clusterOut.getDouble(1) - clusterIn.getDouble(1), 2));
//                proximityMatrix.getProximityMatrix()[i][j] = sqrt;
                try {
                    EuclValue e = new EuclValue(sqrt, i, j);
                    proximityMatrix.getEuclValues().add(e);
                } catch (Exception e) {
                    System.out.println(i+":"+j);
                }
            }
        }
        Collections.sort(proximityMatrix.getEuclValues(), new EuclValueCompare());
    }


    public void buildClassifier() throws Exception {
        while (proximityMatrix.getEuclValues().size() > 0) { // we run the classifier until the number of clusters become 1
            EuclValue remove = proximityMatrix.getEuclValues().get(0);
            int x = remove.getX();
            int y = remove.getY();
            logger.info("Proximity Value:" + remove.getValue() + " has removed from Heap");
            if (x != y) { // not the same cluster
                Cluster cluster1 = clusterList.get(x);

                Cluster cluster2 = clusterList.get(y);
                if ((ClusterUtils.getRootCluster(clusterList, cluster1).getId() != // this makesure selected nodes are already under the same root, in this case we don't merge
                        ClusterUtils.getRootCluster(clusterList, cluster2).getId())) {
                    ClusterUtils.mergeClusters(clusterList, cluster1, cluster2);
                } else {
                    logger.info("Two clusters are already under same root, so we skip");
                }
            }
            proximityMatrix.getEuclValues().remove(0);
        }
    }

    @Override
    public void printResults() {

    }

    private void printCluster(Cluster cluster) {
        if (cluster.getChildren().size() > 0) {
            printCluster(clusterList.get(cluster.getChildren().get(0)));
            printCluster(clusterList.get(cluster.getChildren().get(1)));
        } else {
            Tuple data = cluster.getData();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < data.size(); i++) {
                if (data.getValue(i) instanceof String) {
                    buffer.append(data.getString(i));
                } else if (data.getValue(i) instanceof Integer) {
                    buffer.append(data.getInteger(i));
                } else if (data.getValue(i) instanceof Double) {
                    buffer.append(data.getDouble(i));
                }
            }
        }
    }

    public ProximityMatrix getProximityMatrix() {
        return proximityMatrix;
    }

    public void setProximityMatrix(ProximityMatrix proximityMatrix) {
        this.proximityMatrix = proximityMatrix;
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }

    public void setClusterList(List<Cluster> clusterList) {
        this.clusterList = clusterList;
    }
}
