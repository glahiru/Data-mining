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
package com.datamining.agglomerative.utils;

import com.datamining.agglomerative.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class ClusterUtils {
    private final static Logger logger = LoggerFactory.getLogger(ClusterUtils.class);

    /* we don't recursively store cluster*/
    public static void saveCluster(Cluster cluster) {
        try
        {
            String path = File.separator + "tmp" + File.separator + cluster.getId();
            FileOutputStream fileOut =
                    new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(cluster);
            out.close();
            fileOut.close();
            logger.info("Serialized data is saved in "+ path);
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    /* we don't recursively load cluster*/
    public static Cluster load(int  clusterId) throws Exception{
        try
        {
            String path = File.separator + "tmp" + File.separator + clusterId;
            FileInputStream fileOut =
                    new FileInputStream(path);
            ObjectInputStream out = new ObjectInputStream(fileOut);
            Cluster cluster = (Cluster)out.readObject();
            out.close();
            fileOut.close();
            logger.info("DeSerialized data is saved in "+ path);
            return cluster;
        }catch(IOException i)
        {
            logger.error("Error reading object");
            i.printStackTrace();
            throw i;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            logger.error("Error reading object");
            throw e;
        }
    }

    public static Cluster mergeClusters(List<Cluster> clusterList,Cluster cluster1,Cluster cluster2) throws Exception {
        Cluster cluster = new Cluster(clusterList.size());
        Cluster rootCluster1 = ClusterUtils.getRootCluster(clusterList, cluster1);
        Cluster rootCluster2 = ClusterUtils.getRootCluster(clusterList, cluster2);

        rootCluster1.setParentId(cluster.getId());
        rootCluster2.setParentId(cluster.getId());

        cluster.addChild(rootCluster1.getId());
        cluster.addChild(rootCluster2.getId());
        clusterList.add(cluster);

        logger.info("------------------------------------------------------------------------------------------------------------");
        logger.info("request to merge cluster1:"+cluster1.getId()+" and cluster2:"+cluster2.getId());
        if(cluster1.getId()!=rootCluster1.getId()){
            logger.info("This cluster:"+ cluster1.getId()+" has a parent so we merge the parent cluster:" + rootCluster1.getId());
        }
        if(cluster2.getId()!=rootCluster2.getId()){
            logger.info("This cluster:"+ cluster2.getId()+" has a parent so we merge the parent cluster:" + rootCluster2.getId());
        }
        logger.info("Size of cluster1: " + ClusterUtils.getSize(clusterList,rootCluster1));
        logger.info("Size of cluster2: "+ClusterUtils.getSize(clusterList,rootCluster2));

        logger.info("merging root cluster:" + cluster1.getId() + " and root cluster:" + cluster2.getId());
        logger.info("Successfully merged cluster1:"+rootCluster1.getId()+" and cluster2:"+rootCluster2.getId()+" created cluster:"+cluster.getId());
//        ClusterUtils.saveCluster(cluster1);
//        ClusterUtils.saveCluster(cluster2);
//        ClusterUtils.saveCluster(cluster);
        return cluster;
    }

    public static Cluster getRootCluster(List<Cluster> clusterList, Cluster cluster) {
        Cluster rootCluster = cluster;
        while (rootCluster.getParentId() != -1) {
            rootCluster = getRootCluster(clusterList, clusterList.get(rootCluster.getParentId()));
        }
        return rootCluster;
    }
    public static int getSize(List<Cluster> clusterList,Cluster cluster) throws Exception {
        int size = 1;
        for(int child:cluster.getChildren()) {
            size += getSize(clusterList,clusterList.get(child));
        }
        return size;
    }
}
