package com.nubank.authorizer.application

import io.circe.syntax._

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._
import scala.collection.mutable.ListBuffer
import com.nubank.authorizer.application.cli.Program
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import com.nubank.authorizer.application.cli.model.json._

class ProgramSpec extends AnyFunSuite {
  def readResource(name: String): List[String] = {
    Files.readAllLines(Paths.get(getClass.getClassLoader.getResource(name).toURI)).asScala.toList
  }

  def scenario(name: String): Assertion = {
    val p = new Program()
    val output: ListBuffer[String] = ListBuffer.empty

    val fileNameIn = s"$name-scenario-in"
    val fileNameOut = s"$name-scenario-out"

    p.run(readResource(fileNameIn), r => output += r.asJson.noSpaces, _ => assert(false))
    assertResult(readResource(fileNameOut))(output.toList)
  }

  test("all scenarios") {
    scenario("account-already-initialized")
    scenario("account-not-initialized")
    scenario("card-not-active")
    scenario("double-transaction")
    scenario("high-frequency-small-interval")
    scenario("insufficient-limit")
    scenario("multiple-violations")
    scenario("no-problem")
  }
}
