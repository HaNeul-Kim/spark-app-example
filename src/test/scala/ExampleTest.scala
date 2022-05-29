package com.tistory.hskimsky

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.model.{AccessControlList, ObjectMetadata, PutObjectResult, Region}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.junit.Test
import org.slf4j.{Logger, LoggerFactory}

import java.io.{File, FileInputStream}
import scala.collection.JavaConverters._

/**
 * @author Haneul, Kim
 * @version 1.0.0
 * @since 2022-05-27
 */
class ExampleTest {
  private val logger: Logger = LoggerFactory.getLogger(classOf[ExampleTest])

  val R2_ENDPOINT_URL = "https://<accountid>.r2.cloudflarestorage.com"
  val R2_ACCESS_KEY_ID = "<access_key_id>"
  val R2_SECRET_KEY_ID = "<access_key_secret>"
  val S3_ENDPOINT_URL = "https://s3.<region>.amazonaws.com"
  val S3_ACCESS_KEY_ID = "<access_key_id>"
  val S3_SECRET_KEY_ID = "<access_key_secret>+gt"

  @Test
  def connect(): Unit = {
    val conf = new Configuration()
    conf.set("fs.s3a.endpoint", R2_ENDPOINT_URL)
    conf.set("fs.s3a.access.key", R2_ACCESS_KEY_ID)
    conf.set("fs.s3a.secret.key", R2_SECRET_KEY_ID)
    // conf.set("fs.s3a.aws.credentials.provider", classOf[org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider].getName)
    // conf.set("fs.s3a.session.token", "")
    // conf.set("fs.s3a.impl", classOf[org.apache.hadoop.fs.s3a.S3AFileSystem].getName)
    // conf.set("fs.AbstractFileSystem.s3a.impl", classOf[org.apache.hadoop.fs.s3a.S3A].getName)

    val bucket = "test-bucket"
    val path = new Path(s"s3a://${bucket}/")
    // val fs: FileSystem = FileSystem.get(conf)
    val fs: FileSystem = path.getFileSystem(conf)
    fs.globStatus(path).foreach(status => {
      logger.info(s"status = ${status}")
    })

    conf.iterator().asScala.toSeq.sortWith((a, b) => a.getKey.compareTo(b.getKey) < 0).foreach(e => println((e.getKey, e.getValue)))
  }

  @Test
  def s3BucketAclTest(): Unit = {
    val region: Region = Region.US_West_2
    val awsCreds = new BasicAWSCredentials(S3_ACCESS_KEY_ID, S3_SECRET_KEY_ID)
    val s3: AmazonS3 = AmazonS3ClientBuilder.standard.
      withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(S3_ENDPOINT_URL, region.toString)).
      withCredentials(new AWSStaticCredentialsProvider(awsCreds)).
      build
    s3.listBuckets().asScala.foreach(b => {
      logger.info(s"b.getName = ${b.getName}")
      val acl: AccessControlList = s3.getBucketAcl(b.getName)
      logger.info(s"acl = ${acl}")
      // 2022-05-29 18:54:41.915  INFO [c.t.hskimsky.ExampleTest      :53] - b.getName = hskimsky-test-bucket
      // 2022-05-29 18:54:43.226  INFO [c.t.hskimsky.ExampleTest      :55] - acl = AccessControlList [owner=S3Owner [name=USERNAME,id=0123456789abcdef], grants=[Grant [grantee=com.amazonaws.services.s3.model.CanonicalGrantee@7f1e7f2a, permission=FULL_CONTROL]]]
    })
  }

  @Test(expected = classOf[com.amazonaws.services.s3.model.AmazonS3Exception])
  def r2BucketAclTest(): Unit = {
    val r2Creds = new BasicAWSCredentials(R2_ACCESS_KEY_ID, R2_SECRET_KEY_ID)
    val r2: AmazonS3 = AmazonS3ClientBuilder.standard.
      withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(R2_ENDPOINT_URL, "auto")).
      withCredentials(new AWSStaticCredentialsProvider(r2Creds)).
      build
    r2.listBuckets().asScala.foreach(b => {
      logger.info(s"b.getName = ${b.getName}")
      val acl: AccessControlList = r2.getBucketAcl(b.getName)
      logger.info(s"acl = ${acl}")
    })
  }

  @Test
  def r2PutObjectTest(): Unit = {
    val r2Creds = new BasicAWSCredentials(R2_ACCESS_KEY_ID, R2_SECRET_KEY_ID)
    val r2: AmazonS3 = AmazonS3ClientBuilder.standard.
      withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(R2_ENDPOINT_URL, "auto")).
      withCredentials(new AWSStaticCredentialsProvider(r2Creds)).
      build
    val objectMetadata = new ObjectMetadata()
    objectMetadata.setContentType("yaml")
    logger.info(s"objectMetadata = ${objectMetadata}")
    val result: PutObjectResult = r2.putObject("test-bucket", "k8s_xen_content_type.yaml", new FileInputStream(new File("F:\\vm\\k8s_xen.yaml")), objectMetadata)
    logger.info(s"result = ${result}")
  }
}
