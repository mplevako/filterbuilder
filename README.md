# Query Filter Builder

 Imagine that an external system provides you an API to query for data. Datum (an item) is described by a set of key-value pairs (called properties).
 Basically, you can either run an exact query (for items matching a particular property value) or you can run range queries. You can also compose
 boolean expressions out of these queries.
 This project implements a parser able to build filters for querying data from an external system. The parser recognizes a friendly expression language conforming to the following grammar

```
#!bnf

<string>   ::= quoted string literal
<integer>  ::= base-10 integer literal
<value>    ::= <string> | <integer>
<property> ::= "prop" "[" <string> "]"
<disjunct-expr> ::= <conjunct-expr> ("||" <conjunct-expr>)*
<conjunct-expr> ::= <atom-expr> ("&&" <atom-expr>)*
<atom-expr>     ::= "(" <disjunct-expr> ")" | <base-expr>
<base-expr>     ::= <property> "="  <value> |
                    <property> ">"  <value> |
                    <property> ">=" <value> |
                    <property> "<"  <value> |
                    <property> "<=" <value>

```

and is intended to be included into an external system API's implementation by mixing the filters.impl.FilterBuilder trait.

## Setup
 To build the project you should install SBT (http://www.scala-sbt.org/). Having installed it simply type
 
 ```
$ sbt compile
```

to build the project and 

```
$ sbt test
```

to test it.