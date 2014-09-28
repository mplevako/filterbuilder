/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.api

sealed trait Filter {
}

// Accepts an item iff all descendant filters accept the item.
trait AndFilter extends Filter {
  def must(f: Filter)
}

// Accepts an item iff any of descendant filters accept the item.
trait OrFilter extends Filter {
  def either(f: Filter)
}

// Accepts an item iff its property value falls within the specified range.
trait RangeFilter extends Filter {
  def prop(): String
  def setLowerBound(lb: Any, inclusive: Boolean = false)
  def setUpperBound(ub: Any, inclusive: Boolean = false)
  def clearLowerBound()
  def clearUpperBound()
}

// Accepts an item iff its property value matches one of the specified values.
trait ExactFilter extends Filter {
  def prop(): String
  def add(v: Any)
}
