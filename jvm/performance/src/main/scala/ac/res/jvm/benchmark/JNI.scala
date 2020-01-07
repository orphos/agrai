package ac.res.jvm.benchmark

import java.nio.file.Files
import java.nio.file.StandardOpenOption

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils

object JNI {
  val ext = if (SystemUtils.IS_OS_WINDOWS) "dll" else if (SystemUtils.IS_OS_MAC_OSX) "dylib" else "so"
  val tmp = Files.createTempFile("libjvm-benchmark", "." + ext)
  IOUtils.copy(
    getClass.getClassLoader.getResourceAsStream("libjvm-benchmark." + ext),
    Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
  )
  println(tmp)

  System.load(tmp.toAbsolutePath.toString)

  def apply(): JNI = new JNI()
}

class JNI private () {
  @native def addDoubleExact(left: Double, right: Double): Double
}
