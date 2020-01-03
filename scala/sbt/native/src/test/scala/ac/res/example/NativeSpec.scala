package ac.res.example

import org.scalatest.diagrams.Diagrams
import org.scalatest.funsuite.AnyFunSuite

class NativeSpec extends AnyFunSuite with Diagrams {
  test("call native function") {
    println(Native.Lib.native_getpid())
  }
}
