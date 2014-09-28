/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import filters.api._
import filters.impl.FilterBuilder
import org.easymock.EasyMock

class FilterBuilderMock(delegate: FilterQuery) extends FilterBuilder {
  override def query(f: Filter): Seq[Item] = null
  override def createExactFilter(prop: String): ExactFilter = delegate.createExactFilter(prop)
  override def createAndFilter(): AndFilter = delegate.createAndFilter()
  override def createOrFilter(): OrFilter = delegate.createOrFilter()
  override def createRangeFilter(prop: String): RangeFilter = delegate.createRangeFilter(prop)
  def reset() = EasyMock.reset(delegate)
  def replay() = EasyMock.replay(delegate)
  def verify() = EasyMock.verify(delegate)
}
