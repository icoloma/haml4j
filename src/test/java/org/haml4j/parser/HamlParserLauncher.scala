package org.haml4j.parser
import org.junit._
import org.parboiled.scala.ParsingResult
import org.parboiled.scala.parserunners.ReportingParseRunner
import org.parboiled.buffers.IndentDedentInputBuffer
import org.parboiled.common.FileUtils
import org.parboiled.scala.Input
import java.io.InputStream
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

class HamlParserLauncher { 

  val parser = new HamlParser()
  
  def parse(f: String) {
    val runner = ReportingParseRunner(parser.FileTemplate);
    val filename = "org/haml4j/parser/" + f;
    val fileContents = FileUtils.readAllCharsFromResource(filename);
    if (fileContents == null)
      throw new IllegalArgumentException("Cannot find '" + filename + "' in classpath");
	val result = runner.run(
	    new Input(fileContents).transformIndents(2, "-#")
	);
  }

}