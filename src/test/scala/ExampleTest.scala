package com.tistory.hskimsky

import org.junit.Test
import org.slf4j.{Logger, LoggerFactory}

/**
 * @author Haneul, Kim
 * @version 1.0.0
 * @since 2022-05-27
 */
class ExampleTest {
  private val logger: Logger = LoggerFactory.getLogger(classOf[ExampleTest])

  @Test
  def testMethod1(): Unit = {
    logger.info("this is test method")
  }
}
