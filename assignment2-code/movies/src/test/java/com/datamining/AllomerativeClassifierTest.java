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

import com.datamining.agglomerative.AgglomerativeClassfier;
import com.datamining.agglomerative.Tuple;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class AllomerativeClassifierTest extends BaseTest{
    private final static Logger logger = LoggerFactory.getLogger(AllomerativeClassifierTest.class);

    @Test
    public void compareAgeOccupation() throws Exception {
        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost/test";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myUrl, "root", "");

        String countQuery = "select count(occupation) as rowcount1 from users";
        PreparedStatement preparedStmt = conn.prepareStatement(countQuery);
        ResultSet rs = preparedStmt.executeQuery(countQuery);
        int count = 0;
        if(rs.next()) {
            count = rs.getInt("rowcount1");
        }
        AgglomerativeClassfier agglomerativeClassfier = new AgglomerativeClassfier(count);

        String query = "select age,occupation from users";
        preparedStmt = conn.prepareStatement(query);
        rs = preparedStmt.executeQuery(query);

        while(rs.next()) {
            double occupation = (double)rs.getInt("occupation");
            double age = Double.parseDouble(rs.getString("age"));
            ArrayList<Object> objects = new ArrayList<Object>();
            // we need to add first two values in the binary classification, if there are
            // extra attributes those should be added after these two
            objects.add(occupation);
            objects.add(age);
//            objects.add(rs.getString("userId"));
            // nowe we add userId to distinguish users


            Tuple tuple = new Tuple(objects);
            agglomerativeClassfier.loadClassifier(tuple);
        }

        agglomerativeClassfier.calcProximityMatrix();

        agglomerativeClassfier.buildClassifier();
        logger.info("Total data object count:" + count);

    }

    @Test
    public void compareGenreAndRatings() throws Exception {
        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost/test";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myUrl, "root", "");

        String countQuery = "select count(occupation) as rowcount1 from users";
        PreparedStatement preparedStmt = conn.prepareStatement(countQuery);
        ResultSet rs = preparedStmt.executeQuery(countQuery);
        int count = 0;
        if(rs.next()) {
            count = rs.getInt("rowcount1");
        }
        AgglomerativeClassfier agglomerativeClassfier = new AgglomerativeClassfier(count);

        String query = "select age,occupation from users";
        preparedStmt = conn.prepareStatement(query);
        rs = preparedStmt.executeQuery(query);

        while(rs.next()) {
            double occupation = (double)rs.getInt("occupation");
            double age = Double.parseDouble(rs.getString("age"));
            ArrayList<Object> objects = new ArrayList<Object>();
            // we need to add first two values in the binary classification, if there are
            // extra attributes those should be added after these two
            objects.add(occupation);
            objects.add(age);
//            objects.add(rs.getString("userId"));
            // nowe we add userId to distinguish users


            Tuple tuple = new Tuple(objects);
            agglomerativeClassfier.loadClassifier(tuple);
        }

        agglomerativeClassfier.calcProximityMatrix();

        agglomerativeClassfier.buildClassifier();
        logger.info("Total data object count:" + count);

    }
}
