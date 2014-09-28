/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import filters.api._
import org.easymock.EasyMock
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar

import scala.util.Success

trait ExactFilterSpec extends BaseFilterSpec with EasyMockSugar{ this: WordSpec =>

  def exactFilterBuilder() {

    def withFilters(testCode: ( ExactFilter, FilterBuilderMock) => Any) {
      val exactFilter = mock[ExactFilter]
      val delegate = mock[FilterQuery]
      val filterQuery = new FilterBuilderMock(delegate)
      try {
        testCode(exactFilter, filterQuery)
      }
      finally {
        EasyMock.reset(exactFilter)
        filterQuery.reset()
      }
    }

    def mockExactFilter(filterQuery: FilterQuery, filter: ExactFilter, propValues: Any*) {
      for(propValue <- propValues) {
        filterQuery.createExactFilter(prop).andReturn(filter)
        filter.add(propValue)
      }
    }

    def checkBuilderSuccessPostConditions(filterQuery: FilterQuery, expr: String, postCondition: ExactFilter => Unit) = filterQuery.buildFilter(expr) match {
      case Success(filter: ExactFilter) => postCondition(filter)
      case _ => fail()
    }

    def assertHasProperty(expected: ExactFilter)(actual: ExactFilter) {
      assertFilterMatch(expected)(actual)
      actual.prop() == prop
    }

    "filterBuilder" must construct{
      "an ExactFilter" which {
        "is created via the Filters.createExactFilter factory method" in withFilters { (filter, filterQuery) =>
          expecting {
            mockExactFilter(filterQuery, filter, stringValue)
          }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]="$stringValue"""", assertFilterMatch(filter))
          }
        }

        """has its "name" property set if the <property> part has the form "prop" "[" name "]"""" in withFilters { (filter, filterQuery) =>
          expecting {
            mockExactFilter(filterQuery, filter, stringValue)
            filter.prop().andReturn(prop)
          }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]="$stringValue"""", assertHasProperty(filter))
          }
        }

        "has its add method called with the <value> argument for string values" in withFilters { (filter, filterQuery) =>
          expecting {
            mockExactFilter(filterQuery, filter, stringValue)
            filter.prop().andReturn(prop)
          }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]="$stringValue"""", assertHasProperty(filter))
          }
        }

        "has its add method called with the <value> argument for integer values" in withFilters { (filter, filterQuery) =>
          expecting {
            mockExactFilter(filterQuery, filter, integerValue)
            filter.prop().andReturn(prop)
          }

          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderSuccessPostConditions(filterQuery, s"""prop["$prop"]=$integerValue""", assertHasProperty(filter))
          }
        }

        "does not accept invalid values" in withFilters { (filter, filterQuery) =>
          val nonValidValue = 4.2
          expecting { /*Nothing*/ }
          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]=$nonValidValue""")
          }
        }

        "does not accept empty properties" in withFilters { (filter, filterQuery) =>
          val nonValidValue = true
          expecting { /*Nothing*/ }
          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderFailurePostConditions(filterQuery, s"""prop[]=$stringValue""")
          }
        }

        "does not accept empty values" in withFilters { (filter, filterQuery) =>
          val nonValidValue = true
          expecting { /*Nothing*/ }
          whenExecutingBuilder(filterQuery, filter) {
            checkBuilderFailurePostConditions(filterQuery, s"""prop["$prop"]=""")
          }
        }
      }
    }
  }
}
