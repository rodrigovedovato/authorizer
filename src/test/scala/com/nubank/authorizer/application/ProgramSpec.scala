package com.nubank.authorizer.application

import io.circe.syntax._

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._
import scala.collection.mutable.ListBuffer
import com.nubank.authorizer.application.cli.Program
import org.scalatest.funsuite.AnyFunSuite

class ProgramSpec extends AnyFunSuite {
  def readResource(name: String): List[String] = {
    Files.readAllLines(Paths.get(ClassLoader.getSystemResource(name).toURI)).asScala.toList
  }

  test("no problem-scenario") {
    val p = new Program()
    val output: ListBuffer[String] = ListBuffer.empty

    p.run(readResource("no-problem-scenario-in"), r => output += r.asJson.noSpaces, _ => assert(false))

    assertResult(readResource("no-problem-scenario-out"))(output.toList)
  }
}
