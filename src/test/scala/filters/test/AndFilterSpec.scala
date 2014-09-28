/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import filters.api._
import org.easymock.EasyMock
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar

import scala.util.Success

trait AndFilterSpec extends BaseFilterSpec with EasyMockSugar{ this: WordSpec =>

  def withFilters(testCode: (AndFilter, FilterBuilderMock, OrFilter, ExactFilter, RangeFilter) => Any) {
    val exactFilter = mock[ExactFilter]
    val rangeFilter = mock[RangeFilter]
    val andFilter = mock[AndFilter]
    val orFilter = mock[OrFilter]
    val delegate = mock[FilterQuery]
    val filterQuery = new FilterBuilderMock(delegate)
    try {
      testCode(andFilter, filterQuery, orFilter, exactFilter, rangeFilter)
    }
    finally {
      EasyMock.reset(andFilter)
      EasyMock.reset(orFilter)
      EasyMock.reset(exactFilter)
      EasyMock.reset(rangeFilter)
      filterQuery.reset()
    }
  }

  def mockOrFilter(filterQuery: FilterQuery, filter: AndFilter, orFilter: OrFilter, exactFilter: ExactFilter, rangeFilter: RangeFilter) {
    filterQuery.createAndFilter().andReturn(filter)
    filterQuery.createOrFilter().andReturn(orFilter)
    filterQuery.createExactFilter(prop).andReturn(exactFilter)
    orFilter.either(exactFilter)
    filter.must(orFilter)
    filterQuery.createRangeFilter(prop).andReturn(rangeFilter).times(2)
    orFilter.either(orFilter)
    filter.must(rangeFilter)
  }

  def checkBuilderSuccessPostConditions(filterQuery: FilterQuery, expr: String, postCondition: AndFilter => Unit) = filterQuery.buildFilter(expr) match {
    case Success(filter: AndFilter) =>
      postCondition(filter)
    case _ =>
      fail()
  }

  def andFilterBuilder() {
    "filterBuilder" must construct{
      "an AndFilter via the Filters.createAndFilter factory method and brackets are respected" in withFilters { (filter, filterQuery, orFilter, exactFilter, rangeFilter) =>
        expecting {
          mockOrFilter(filterQuery, filter, orFilter, exactFilter, rangeFilter)
        }

        whenExecutingBuilder(filterQuery, filter) {
          checkBuilderSuccessPostConditions(filterQuery, s"""((prop["$prop"]="$stringValue" || prop[ "$prop"] >= $lowerIntBound) && (prop[ "$prop"] < "$upperStringBound"))""", assertFilterMatch(filter))
        }
    }
  }
    }
}
