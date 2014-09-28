/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.api

import scala.util.Try

/**
 * Imagine that an external system provides you an API to query for data.
 * Datum (an item) is described by a set of key-value pairs (called properties).
 * Basically, you can either run an exact query (for items matching a particular
 * property value) or you can run range queries. You can also compose
 * boolean expressions out of these queries.
 */
trait FilterQuery {
  // Queries an external system for items matching the specified filter.
  def query(f: Filter): Seq[Item]

  // Factory method.
  def createAndFilter(): AndFilter

  // Factory method.
  def createOrFilter(): OrFilter

  // Factory method.
  def createRangeFilter(prop: String): RangeFilter

  // Factory method.
  def createExactFilter(prop: String): ExactFilter

  def buildFilter(expr: String): Try[Filter]
}