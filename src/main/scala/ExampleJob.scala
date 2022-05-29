package com.tistory.hskimsky

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.util.LongAccumulator
import org.slf4j.LoggerFactory

/**
 * @author Haneul, Kim
 * @version 1.0.0
 * @since 2022-05-25
 */
object ExampleJob {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val Array(userId, now, query) = args
    logger.info(s"userId = ${userId}, now = ${now}, query = ${query}")
    /*
    spark-shell \
     --conf spark.hadoop.fs.s3a.endpoint=https://HOSTNAME \
     --conf spark.hadoop.fs.s3a.access.key=AWS_ACCESS_KEY_ID \
     --conf spark.hadoop.fs.s3a.secret.key=AWS_SECRET_ACCESS_KEY
    */
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().getOrCreate()
    val sc: SparkContext = spark.sparkContext

    // val count: Long = sc.textFile(inputPath.toString).count()
    // logger.info(s"count = ${count}")
    val df: DataFrame = spark.read.format("jdbc").
      option("url", "jdbc:kudu:schemaFactory=com.twilio.kudu.sql.schema.DefaultKuduSchemaFactory;schema=default;timeZone=ko_KR;caseSensitive=false;schema.connect=adm1.sky.local:7051,hdm1.sky.local:7051,hdm2.sky.local:7051").
      // .option("dbtable", "\"default.log_range\"")
      option("query", query).
      option("driver", "org.apache.calcite.jdbc.KuduDriver").
      load
    val countAccumulator: LongAccumulator = sc.longAccumulator("all counter")
    df.rdd.foreach(_ => countAccumulator.add(1))
    df.write.format("csv").save(s"/tmp/${userId}/${now}")

    spark.stop()
  }
}
