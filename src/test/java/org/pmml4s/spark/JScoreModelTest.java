/*
 * Copyright (c) 2017-2019 AutoDeploy AI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pmml4s.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Examples how to use PMML4S-Spark in Java
 */
public class JScoreModelTest {

    private SparkSession spark = SparkSession
            .builder()
            .master("local[*]")
            .getOrCreate();
    private Dataset<?> df = spark.read()
            .format("csv")
            .option("header", "true")
            .option("inferSchema", "true")
            .load("src/test/resources/data/Iris.csv");

    @Test
    public void testScoreModelWithDefaultSettings() {
        ScoreModel model = ScoreModel.fromFile("src/test/resources/models/single_iris_dectree.xml");
        Dataset<?> scoreDf = model.transform(df);
        assertEquals(11, scoreDf.schema().size());
        assertEquals("predicted_class", scoreDf.schema().fields()[5].name());
        scoreDf.printSchema();
        scoreDf.show();
        fail();
    }

    @Test
    public void testScoreModelWithAppendInputsEqualsFalse() {
        ScoreModel model = ScoreModel.fromFile("src/test/resources/models/single_iris_dectree.xml");
        Dataset<?> scoreDf = model
                .setPrependInputs(false)
                .transform(df);
        assertEquals(6, scoreDf.schema().size());
        assertEquals("predicted_class", scoreDf.schema().fields()[0].name());
        scoreDf.printSchema();
        scoreDf.show();
    }

    @Test
    public void testScoreModelWithAppendInputsEqualsFalseAndPredictionColEqualsPrediction() {
        ScoreModel model = ScoreModel.fromFile("src/test/resources/models/single_iris_dectree.xml");
        Dataset<?> scoreDf = model
                .setPrependInputs(false)
                .setPredictionCol("prediction")
                .transform(df);
        assertEquals(6, scoreDf.schema().size());
        assertEquals("prediction", scoreDf.schema().fields()[0].name());
        scoreDf.printSchema();
        scoreDf.show();
    }
}
