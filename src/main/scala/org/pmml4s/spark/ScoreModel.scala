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
package org.pmml4s.spark

import java.io.{File, InputStream}

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{BooleanParam, Param, ParamMap, Params}
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.pmml4s.data.Series
import org.pmml4s.metadata.ResultFeature
import org.pmml4s.model.Model
import org.pmml4s.util.Utils

import scala.io.Source

/**
 * Implements the prediction transform. This transformer takes in a PMML model.
 */
class ScoreModel(
                  override val uid: String,
                  val model: Model)
  extends Transformer with ScoreParams {

  /**
   * Constructs a score model with a model of PMML4S.
   *
   * @param model A model of PMML4S.
   */
  def this(model: Model) =
    this(Identifiable.randomUID("scoreModel"), model)

  /** @group setParam */
  def setPrependInputs(value: Boolean): this.type = set(prependInputs, value)

  /** @group setParam */
  def setPredictionCol(value: String): this.type = set(predictionCol, value)

  override def transform(dataset: Dataset[_]): DataFrame = {
    val inputSchema = model.inputSchema
    val nameToIndex = dataset.schema.fieldNames.zipWithIndex.toMap
    val indexWithDataType = inputSchema.map(x => (nameToIndex.get(x.name), x.dataType))

    val rdd = dataset.toDF.rdd.mapPartitions(x => {
      for (row <- x) yield {
        val values = indexWithDataType.map(y => y._1.map(index => Utils.toVal(row.get(index), y._2)).orNull)
        val series = model.predict(Series.fromSeq(values))
        val outRow = Row.fromSeq(series.toSeq)
        if ($(prependInputs)) {
          Row.merge(row, outRow)
        } else {
          outRow
        }
      }
    })

    dataset.sqlContext.createDataFrame(rdd, transformSchema(dataset.schema))
  }

  override def copy(extra: ParamMap): Transformer = defaultCopy(extra)

  override def transformSchema(schema: StructType): StructType = {
    // check if predictionCol is set
    val namesMap: Map[String, String] = if (isSet(predictionCol)) {
      val field = model.outputFields.find(x => x.feature == ResultFeature.predictedValue)
      field.map(x => Map(x.name -> $(predictionCol))).getOrElse(Map.empty)
    } else Map.empty

    val outputFields = model.outputSchema.map(x =>
      StructField(namesMap.getOrElse(x.name, x.name), ScoreModel.toSpark(x.dataType), true))
    if (getPrependInputs) {
      StructType(schema.fields ++ outputFields)
    } else {
      StructType(outputFields)
    }
  }
}

object ScoreModel {

  /** Constructs a score model from PMML file with given pathname. */
  def fromFile(name: String): ScoreModel = apply(Source.fromFile(name))

  /** Constructs a score model from PMML file with given the Java file object. */
  def fromFile(file: File): ScoreModel = apply(Source.fromFile(file))

  /** Constructs a score model from PMML a String. */
  def fromString(s: String): ScoreModel = apply(Source.fromString(s))

  /** Constructs a score model from PMML in array of bytes. */
  def fromBytes(bytes: Array[Byte]): ScoreModel = apply(Source.fromBytes(bytes))

  /** Constructs a score model from PMML in an inputs stream. */
  def fromInputStream(is: InputStream): ScoreModel = apply(Source.fromInputStream(is));

  /** Constructs a score model from an IO source. */
  def apply(source: Source): ScoreModel = apply(Model(source))

  /** Constructs a score model from a PMML model. */
  def apply(model: Model): ScoreModel = new ScoreModel(model)

  /** Converts data type of PMML to Spark's */
  def toSpark(dataType: org.pmml4s.common.DataType): org.apache.spark.sql.types.DataType = dataType match {
    case org.pmml4s.common.StringType  => org.apache.spark.sql.types.StringType
    case org.pmml4s.common.BooleanType => org.apache.spark.sql.types.BooleanType
    case org.pmml4s.common.IntegerType => org.apache.spark.sql.types.LongType
    case _                             => org.apache.spark.sql.types.DoubleType
  }
}

/**
 * Params for score model of PMML.
 */
trait ScoreParams extends Params {
  /**
   * Param for whether to prepend the input cols to the output data.
   *
   * @group param
   */
  final val prependInputs: BooleanParam = new BooleanParam(this, "prependInputs", "whether to append the input cols to the output data.")

  setDefault(prependInputs, true)

  /** @group getParam */
  final def getPrependInputs: Boolean = $(prependInputs)

  /**
   * Param for prediction column name.
   *
   * @group param
   */
  final val predictionCol: Param[String] = new Param[String](this, "predictionCol", "prediction column name")

  /** @group getParam */
  final def getPredictionCol: String = $(predictionCol)
}
