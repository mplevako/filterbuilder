/**
 * Copyright 2014 Maxim Plevako
 **/
package filters.test

import org.scalatest.WordSpec

class FilterBuilderSpec extends WordSpec with ExactFilterSpec with RangeFilterSpec with OrFilterSpec with AndFilterSpec{

  "A FilterBuilder" when {
      "the expression is empty" must {
        "produce nothing?" in {

        }
      }

      """sees an expression like <property> = <value>""" should {
        behave like exactFilterBuilder
      }

      """sees an expression like <conjunct-expr> ("||" <conjunct-expr>)*""" should {
        behave like orFilterBuilder
      }

      """sees an expression like <atom-expr> ("&&" <atom-expr>)*""" should {
        behave like andFilterBuilder
      }

      """sees an expression like <property> <relop> <value> where relop is one of >,>=,< or <=""" should {
        behave like rangeFilterBuilder
      }

      """throw an exception when sees something like <property> = <value> and <property> <one of,>=,< or <= > <value> together?""" in {

      }
    }
}
