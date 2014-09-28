/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import filters.api.{ExactFilter, Filter, FilterQuery, RangeFilter}
import org.easymock.EasyMock
import org.scalatest.WordSpec

import scala.util.Success

trait BaseFilterSpec{ this: WordSpec =>
  val prop = "prop"
  val lowerStringBound = "zero"
  val stringValue = "forty two"
  val upperStringBound = stringValue
  val integerValue = 42
  val lowerIntBound = 0
  val upperIntBound = integerValue

  def construct = afterWord("construct")

  def mockFilterQuery() {
    EasyMock.createMockBuilder(classOf[FilterBuilderMock]).
      addMockedMethod("createAndFilter").
      addMockedMethod("createOrFilter").
      addMockedMethod("createRangeFilter").
      addMockedMethod("createExactFilter").
    createMock()
  }

  def checkBuilderFailurePostConditions(filterQuery: FilterQuery, expr: String) = filterQuery.buildFilter(expr) match {
    case Success(filter) => fail()
    case _ =>
  }

  def assertHasProperty(expected: ExactFilter)(actual: ExactFilter) {
    assertHasProperty(expected)(actual)
  }

  def assertHasProperty(expected: RangeFilter)(actual: RangeFilter) {
    assertHasProperty(expected)(actual)
  }

  def assertFilterMatch[F <: Filter](expected: F)(actual: F) = assert(actual == expected)

  def whenExecutingBuilder(filterBuilderMock: FilterBuilderMock, mocks: AnyRef*)(fun: => Unit) = {

    require(mocks.length > 0, "Must pass at least one mock to whenExecuting, but mocks.length was 0.")

    filterBuilderMock.replay()
    for (m <- mocks)
      EasyMock.replay(m)

    fun

    // Don't put this in a try block, so that if fun throws an exception
    // it propagates out immediately and shows up as the cause of the failed test
    filterBuilderMock.verify()
    for (m <- mocks)
      EasyMock.verify(m)
  }
}
