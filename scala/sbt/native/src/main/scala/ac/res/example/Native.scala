package ac.res.example

import java.nio.file.Files
import java.nio.file.StandardOpenOption

import jnr.ffi.LibraryLoader
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils

object Native {
  trait LibNativeExample {
    def native_getpid(): Long
  }
  val ext = if (SystemUtils.IS_OS_WINDOWS) "dll" else if (SystemUtils.IS_OS_MAC_OSX) "dylib" else "so"
  val tmp = Files.createTempFile("libnative-example", "." + ext)
  IOUtils.copy(
    getClass.getClassLoader.getResourceAsStream("libnative-example." + ext),
    Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
  )
  println(tmp)
  val Lib = LibraryLoader.create(classOf[LibNativeExample]).load(tmp.toAbsolutePath.toString)
}
