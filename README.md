# PMML4S-Spark
_PMML4S-Spark_ is a PMML (Predictive Model Markup Language) scoring library for Spark as SparkML Transformer.

## Features
_PMML4S-Spark_ is the Spark wrapper of _PMML4S_, you can see [PMML4S](https://github.com/autodeployai/pmml4s) for details.

## Prerequisites
 - Spark >= 2.0.0

## Installation
_PMML4S-Spark_ is available from maven central.

Latest release: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.pmml4s/pmml4s-spark_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.pmml4s/pmml4s-spark_2.12)

##### SBT users
```scala
libraryDependencies += "org.pmml4s" %%  "pmml4s-spark" % "0.9.6"
```

##### Maven users
```xml
<dependency>
  <groupId>org.pmml4s</groupId>
  <artifactId>pmml4s-spark_${scala.version}</artifactId>
  <version>0.9.6</version>
</dependency>
```

## Use PMML for Spark in Scala
1. Load model.

    ```scala
    import scala.io.Source
    import org.pmml4s.model.Model
    import org.pmml4s.spark.ScoreModel

    // The main constructor accepts an object of org.pmml4s.model.Model
    val model = ScoreModel(Model(Source.fromURL(new java.net.URL("http://dmg.org/pmml/pmml_examples/KNIME_PMML_4.1_Examples/single_iris_dectree.xml"))))
    ```
    or
    ```scala
    import org.pmml4s.spark.ScoreModel
    
    // load model from those help methods, e.g. pathname, file object, a string, an array of bytes, or an input stream.
    val model = ScoreModel.fromFile("single_iris_dectree.xml")
    ```

2. Call `transform(dataset)` to run a batch score against an input dataset.

    ```scala
    // The data is from http://dmg.org/pmml/pmml_examples/Iris.csv
    val df = spark.read.
      format("csv").
      options(Map("header" -> "true", "inferSchema" -> "true")).
      load("Iris.csv")
 
    val scoreDf = model.transform(df)
    scala> scoreDf.show(5)
    +------------+-----------+------------+-----------+-----------+---------------+-----------+-----------------------+---------------------------+--------------------------+-------+
    |sepal_length|sepal_width|petal_length|petal_width|      class|predicted_class|probability|probability_Iris-setosa|probability_Iris-versicolor|probability_Iris-virginica|node_id|
    +------------+-----------+------------+-----------+-----------+---------------+-----------+-----------------------+---------------------------+--------------------------+-------+
    |         5.1|        3.5|         1.4|        0.2|Iris-setosa|    Iris-setosa|        1.0|                    1.0|                        0.0|                       0.0|      1|
    |         4.9|        3.0|         1.4|        0.2|Iris-setosa|    Iris-setosa|        1.0|                    1.0|                        0.0|                       0.0|      1|
    |         4.7|        3.2|         1.3|        0.2|Iris-setosa|    Iris-setosa|        1.0|                    1.0|                        0.0|                       0.0|      1|
    |         4.6|        3.1|         1.5|        0.2|Iris-setosa|    Iris-setosa|        1.0|                    1.0|                        0.0|                       0.0|      1|
    |         5.0|        3.6|         1.4|        0.2|Iris-setosa|    Iris-setosa|        1.0|                    1.0|                        0.0|                       0.0|      1|
    +------------+-----------+------------+-----------+-----------+---------------+-----------+-----------------------+---------------------------+--------------------------+-------+
    only showing top 5 rows
    ```
    
## Use PMML for Spark in Java
1. Load model.

    ```java
    import org.pmml4s.spark.ScoreModel;
    
    // load model from those help methods, e.g. pathname, file object, a string, an array of bytes, or an input stream.
    ScoreModel model = ScoreModel.fromFile("single_iris_dectree.xml");
    ```

2. Call `transform(dataset)` to run a batch score against an input dataset.

    ```java
    import org.apache.spark.sql.Dataset;
 
    // The data is from http://dmg.org/pmml/pmml_examples/Iris.csv
    Dataset<?> df = spark.read().
       format("csv").
       option("header", "true").
       option("inferSchema", "true").
       load("Iris.csv"); 
   
    Dataset<?> scoreDf = model.transform(df);
    scoreDf.show(5);
    ```

## Use PMML in PySpark
See the [PyPMML-Spark](https://github.com/autodeployai/pypmml-spark) project. _PyPMML-Spark_ is a Python PMML scoring library for PySpark as SparkML Transformer, it really is the Python API for PMML4s-Spark.

## Use PMML in Scala or Java
See the [PMML4S](https://github.com/autodeployai/pmml4s) project. _PMML4S_ is a PMML scoring library for Scala. It provides both Scala and Java Evaluator API for PMML.

## Use PMML in Python
See the [PyPMML](https://github.com/autodeployai/pypmml) project. _PyPMML_ is a Python PMML scoring library, it really is the Python API for PMML4S.

## Deploy PMML as REST API
See the [AI-Serving](https://github.com/autodeployai/ai-serving) project. _AI-Serving_ is serving AI/ML models in the open standard formats PMML and ONNX with both HTTP and gRPC endpoints.

## Deploy and Manage AI/ML models at scale
See the [DaaS](https://www.autodeploy.ai/) system that deploys AI/ML models in production at scale on Kubernetes.

## Support
If you have any questions about the _PMML4S-Spark_ library, please open issues on this repository.

Feedback and contributions to the project, no matter what kind, are always very welcome. 

## License
_PMML4S-Spark_ is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0).
