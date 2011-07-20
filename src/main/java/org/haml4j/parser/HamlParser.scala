package org.haml4j.parser

import org.parboiled.scala._
import org.parboiled.support.Chars
import org.haml4j.model.ParsedTag
import org.haml4j.model.HamlParsedFile

/**
 * Parse a .haml file.
 * This file has been composed from the JsonParser example in Parboiled
 */
class HamlParser extends Parser {
  
  /*
   
    def Clause = rule {Digit ~ ClauseRest ~ EOI}

    def ClauseRest = rule {zeroOrMore(Operator ~ Digit) ~ anyOf("abcd")}

    def Operator = rule {"+" | "-"}

    def Digit = rule {"0" - "9"}
    
  */
  def FileTemplate = rule { zeroOrMore(Line) }
  
  def Line = rule {
    optional( anyOf(Array(Chars.INDENT, Chars.DEDENT)) ) ~
    push(new ParsedTag) ~ 
    Tag
  }
  
  def Tag = rule { 
    optional(TagName) ~ 
    run() ~
    optional(Id) ~ 
    optional(CssClass)  
  }
  
  def TagName = rule { ("a" - "z") | ("A" - "Z") | ("0" - "9") | "-" | "_" }
  
  def Id = rule { "#" ~  oneOrMore(Character) }
  
  def CssClass = rule { "." ~  oneOrMore(Character) }
  
  def Character = rule { EscapedChar | NormalChar }

  def EscapedChar = rule { "\\" ~ (anyOf("\"\\/bfnrt") | Unicode) }

  def NormalChar = rule { !anyOf("\"\\") ~ ANY }

  def Unicode = rule { "u" ~ HexDigit ~ HexDigit ~ HexDigit ~ HexDigit }
/*
  def Integer = rule { optional("-") ~ (("1" - "9") ~ Digits | Digit) }

  def Digits = rule { oneOrMore(Digit) }

  def Digit = rule { "0" - "9" }
*/
  def HexDigit = rule { "0" - "9" | "a" - "f" | "A" - "Z" }

}
