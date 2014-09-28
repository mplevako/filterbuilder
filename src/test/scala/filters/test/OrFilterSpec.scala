/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import filters.api._
import org.easymock.EasyMock
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar

import scala.util.Success

trait OrFilterSpec extends BaseFilterSpec with EasyMockSugar{ this: WordSpec =>

  def orFilterBuilder() {
    def withFilters(testCode: (OrFilter, FilterBuilderMock, ExactFilter, RangeFilter) => Any) {
      val exactFilter = mock[ExactFilter]
      val rangeFilter = mock[RangeFilter]
      val orFilter = mock[OrFilter]
      val delegate = mock[FilterQuery]
      val filterQuery = new FilterBuilderMock(delegate)
      try {
        testCode(orFilter, filterQuery, exactFilter, rangeFilter)
      }
      finally {
        EasyMock.reset(orFilter)
        EasyMock.reset(exactFilter)
        EasyMock.reset(rangeFilter)
        filterQuery.reset()
      }
    }

    def mockOrFilter(filterQuery: FilterQuery, filter: OrFilter, exactFilter: ExactFilter, rangeFilter: RangeFilter) {
      filterQuery.createOrFilter().andReturn(filter)
      filterQuery.createExactFilter(prop).andReturn(exactFilter)
      filter.either(exactFilter)
      filterQuery.createRangeFilter(prop).andReturn(rangeFilter)
      filter.either(rangeFilter)
    }

    def checkBuilderSuccessPostConditions(filterQuery: FilterQuery, expr: String, postCondition: OrFilter => Unit) = filterQuery.buildFilter(expr) match {
      case Success(filter: OrFilter) =>
        postCondition(filter)
      case _ =>
        fail()
    }

    "filterBuilder" must construct {
      "an OrFilter" which {
        "is created via the Filters.createOrFilter factory method and irrelevant spaces are ignored" in withFilters { (filter, filterQuery, exactFilter, rangeFilter) =>
          expecting {
            mockOrFilter(filterQuery, filter, exactFilter, rangeFilter)
          }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderSuccessPostConditions(filterQuery, s"""(prop["$prop"]="$stringValue" || prop[ "$prop"] >= $lowerIntBound)""", assertFilterMatch(filter))
          }
        }
      }
    }
  }
}
