/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import filters.api.{FilterQuery, RangeFilter}
import org.easymock.EasyMock
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar

import scala.util.Success

trait RangeFilterSpec extends BaseFilterSpec with EasyMockSugar{ this: WordSpec =>

  def rangeFilterBuilder() {
    def withFilters(testCode: (RangeFilter, FilterBuilderMock) => Any) {
      val rangeFilter = mock[RangeFilter]
      val delegate = mock[FilterQuery]
      val filterQuery = new FilterBuilderMock(delegate)
      try {
        testCode(rangeFilter, filterQuery)
      }
      finally {
        EasyMock.reset(rangeFilter)
        filterQuery.reset()
      }
    }

    def mockRangeFilter(filterQuery: FilterQuery, filter: RangeFilter) {
      filterQuery.createRangeFilter(prop).andReturn(filter)
    }

    def checkBuilderSuccessPostConditions(filterQuery: FilterQuery, expr: String, postCondition: RangeFilter => Unit) = filterQuery.buildFilter(expr) match {
      case Success(filter: RangeFilter) => postCondition(filter)
      case _ => fail()
    }

    def checkBuilderFailurePostConditions(filterQuery: FilterQuery, expr: String) = filterQuery.buildFilter(expr) match {
      case Success(filter) => fail()
      case _ =>
    }

    "filterBuilder" must construct {
      "a RangeFilter" which {
        """is created via the Filters.createRangeFilter factory method and supports all of <=, <, >= and >
              as well as string and integer bounds""" in withFilters { (filter, filterQuery) =>
          expecting {
            filterQuery.createRangeFilter(prop).andReturn(filter)
            filter.setLowerBound(lowerStringBound, inclusive = false)
            filterQuery.createRangeFilter(prop).andReturn(filter)
            filter.setUpperBound(upperStringBound, inclusive = false)
            filterQuery.createRangeFilter(prop).andReturn(filter)
            filter.setLowerBound(lowerIntBound, inclusive = true)
            filterQuery.createRangeFilter(prop).andReturn(filter)
            filter.setUpperBound(upperIntBound, inclusive = true)
          }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]>"$lowerStringBound"""", assertFilterMatch(filter))
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]<"$upperStringBound"""", assertFilterMatch(filter))
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]>=$lowerIntBound""", assertFilterMatch(filter))
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]<=$upperIntBound""", assertFilterMatch(filter))
          }
        }

        "does not accept bounds of invalid types" in withFilters { (filter, filterQuery) =>
          expecting { /*Nothing*/ }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]>false""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]<true""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]>=3.14""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]<=4.2""")
          }
        }

        "does not accept empty bounds" in withFilters { (filter, filterQuery) =>
          expecting { /*Nothing*/ }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]>""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]<""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]>=""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]<=""")
          }
        }

        "does not accept empty property names" in withFilters { (filter, filterQuery) =>
          expecting { /*Nothing*/ }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderFailurePostConditions(filterQuery, s"prop[]>$lowerIntBound")
            checkBuilderFailurePostConditions(filterQuery, s"prop[]<$upperIntBound")
            checkBuilderFailurePostConditions(filterQuery, s"""prop[]>="$lowerStringBound"""")
            checkBuilderFailurePostConditions(filterQuery, s"""prop[]<="$upperStringBound"""")
          }
        }
      }
    }
  }
}
