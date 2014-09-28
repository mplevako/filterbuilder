/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.impl

import filters.api._

import scala.util.parsing.combinator.RegexParsers

trait FilterBuilder extends FilterQuery with RegexParsers {
  def string: Parser[String] = "\"" ~> """([^"\p{Cntrl}\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*""".r <~ "\""

  def integer: Parser[Int] = """[+-]?(?<!\.)\b[0-9]+\b(?!\.[0-9])""".r ^^ { _.toInt }

  def value: Parser[Any] = string | integer

  def property: Parser[String] = "prop" ~> '[' ~> string <~ ']'

  def disjunctExpr: Parser[Filter] = conjunctExpr ~ rep("||" ~> conjunctExpr) ^^ {
    case x~Nil => x
    case x~xs =>
      val orFilter = createOrFilter()
      orFilter.either(x)
      xs.foldLeft(orFilter)((resultFilter, filter) => {
        resultFilter.either(filter)
        resultFilter
      })
  }

  def conjunctExpr: Parser[Filter] = atomExpr ~ rep("&&" ~> atomExpr) ^^ {
    case x~Nil => x
    case x~xs =>
      val andFilter = createAndFilter()
      andFilter.must(x)
      xs.foldLeft(andFilter)((resultFilter, filter) => {
        resultFilter.must(filter)
        resultFilter
      })
  }

  def atomExpr: Parser[Filter] = "(" ~> disjunctExpr <~ ")" | baseExpr

  def baseExpr: Parser[Filter] = exactFilter | rangeFilter

  def exactFilter: Parser[ExactFilter] = property ~ ('=' ~> value) ^^ {
    case prop ~ propVal =>
      val exactFilter = createExactFilter(prop)
      exactFilter.add(propVal)
      exactFilter
  }

  def rangeFilter: Parser[RangeFilter] = property ~ ("<=" | "<" | ">=" | ">") ~ value ^^ {
    case prop ~ relOp ~ propValue => buildRangeFilter(prop, relOp, propValue)
  }

  def buildRangeFilter(prop: String, relOp: String, propValue: Any) = {
    val rangeFilter = createRangeFilter(prop)
    relOp match {
      case ">" =>
        rangeFilter.setLowerBound(propValue, inclusive = false)
      case ">=" =>
        rangeFilter.setLowerBound(propValue, inclusive = true)
      case "<=" =>
        rangeFilter.setUpperBound(propValue, inclusive = true)
      case "<" =>
        rangeFilter.setUpperBound(propValue, inclusive = false)
    }
    rangeFilter
  }

  override def buildFilter(expr: String) =
    if(expr == null) scala.util.Failure(new NullPointerException("Filter query is null"))
    else {
      parseAll(conjunctExpr, expr) match {
        case Success(result, _) => scala.util.Success(result)
        case NoSuccess(msg, _) => scala.util.Failure(new Exception(msg))
      }
    }
}

