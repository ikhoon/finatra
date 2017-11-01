package com.twitter.inject

import com.twitter.util.{Await, Future}
import java.util.TimeZone
import org.apache.commons.io.IOUtils
import org.joda.time.{DateTimeZone, Duration}
import org.scalatest._

/**
 * Testing trait which provides the following stackable modification traits:
 *  - [[org.scalatest.BeforeAndAfterAll]]
 *  - [[org.scalatest.BeforeAndAfterEach]]
 *  - [[org.scalatest.Matchers]]
 *  - [[com.twitter.inject.Logging]]
 *
 * This trait is expected to be mixed with a class that extends a core Suite trait,
 * e.g., [[org.scalatest.FunSuite]].
 *
 * While you can use this mixin directly, it is recommended that users extend
 * the [[com.twitter.inject.Test]] abstract class.
 */
trait TestMixin
  extends SuiteMixin
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with Matchers
  with Logging { this: Suite =>

  /* Constructor */

  setUtcTimeZone()

  /* Overrides */

  override protected def afterAll() = {
    super.afterAll()
    pool.executor.shutdown()
  }

  /* Protected */

  protected lazy val pool = PoolUtils.newUnboundedPool("Test " + getClass.getSimpleName)

  protected def setUtcTimeZone() = {
    DateTimeZone.setDefault(DateTimeZone.UTC)
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
  }

  protected def resourceAsString(resource: String) = {
    IOUtils.toString(getClass.getResourceAsStream(resource))
  }

  protected def sleep(duration: Duration, verbose: Boolean = false) {
    if (verbose) {
      println("Starting sleep for " + duration)
    }

    Thread.sleep(duration.getMillis)

    if (verbose) {
      println("Finished sleep for " + duration)
    }
  }

  protected def assertFuture[A](result: Future[A], expected: Future[A]) {
    val resultVal = Await.result(result)
    val expectedVal = Await.result(expected)
    resultVal should equal(expectedVal)
  }

  protected def assertFutureValue[A](result: Future[A], expected: A) {
    val resultVal = Await.result(result)
    val expectedVal = Await.result(Future.value(expected))
    resultVal should equal(expectedVal)
  }

  protected def assertFailedFuture[T <: Throwable: Manifest](result: Future[_]): T = {
    try {
      Await.result(result)
      fail("Expected exception " + manifest[T].runtimeClass + " never thrown")
    } catch {
      case e: Throwable =>
        if (manifest[T].runtimeClass.isAssignableFrom(e.getClass))
          e.asInstanceOf[T]
        else
          fail("Expected exception " + manifest[T].runtimeClass + " but caught " + e)
    }
  }

  protected def bytes(str: String) = {
    str.getBytes("UTF-8")
  }
}
