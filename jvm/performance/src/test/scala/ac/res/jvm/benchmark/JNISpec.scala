package ac.res.jvm.benchmark

import org.scalatest.diagrams.Diagrams
import org.scalatest.funsuite.AnyFunSuite

class JNISpec extends AnyFunSuite with Diagrams {
  test("addDoubleExact") {
    val jni = JNI()
    val bound = math.pow(2, 53)
    var i = 0
    val t1 = System.currentTimeMillis()
    // benchmark
    while (i < 1000000) {
      jni.addDoubleExact(bound - 1, 1).isNaN
      jni.addDoubleExact(bound, 1).isNaN
      i += 1
    }
    val t2 = System.currentTimeMillis()
    println(s"t2 - t1 = ${t2 - t1} ms")
    assert(jni.addDoubleExact(math.pow(2, 53) - 1, 1).toLong == math.pow(2, 53).toLong)
    assert(jni.addDoubleExact(math.pow(2, 53), 1).isNaN)
    assert(jni.addDoubleExact(math.pow(2, 53) - 1, 1).toLong == math.pow(2, 53).toLong)
    assert(jni.addDoubleExact(math.pow(2, 53), 1).isNaN)
    assert(!(math.pow(2, 53) + 1).isNaN)
  }
}
