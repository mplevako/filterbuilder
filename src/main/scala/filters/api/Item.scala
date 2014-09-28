/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.api

/**
 * Imagine that an external system provides you an API to query for data.
 * Datum (an item) is described by a set of key-value pairs (called properties).
 */
trait Item {
  // Returns value for a given property.
  def apply(prop: String): Option[Any]
}
