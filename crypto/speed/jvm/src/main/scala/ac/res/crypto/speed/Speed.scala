// Copyright (C) 2019 Takezoe,Tomoaki <tomoaki3478@res.ac>
// SPDX-License-Identifier: Apache-2.0 WITH LLVM-exception

package ac.res.crypto.speed

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.Security

import org.bouncycastle.jce.provider.BouncyCastleProvider

object Speed extends App {
  val r = new SecureRandom()
  val buf = new Array[Byte](1024)
  r.nextBytes(buf)
  val providers = Security.getProviders
  val hashFunctions = List(
    "MD5",
    "SHA-1",
    "SHA-224",
    "SHA-256",
    "SHA-384",
    "SHA-512",
    "Skein-256-256"
  ) ++ List("160", "256", "384", "512").map(i => s"BLAKE2B-$i") ++
    List("224", "256", "384", "512").map(i => s"SHA3-$i")
  for {
    provider <- providers :+ new BouncyCastleProvider()
    hashFunction <- hashFunctions
  } {
    try {
      val md = MessageDigest.getInstance(hashFunction, provider)
      var i = 0
      val start = System.currentTimeMillis()
      while (System.currentTimeMillis() <= start + 500) {
        md.reset()
        md.update(buf)
        md.digest()
        i += 1
      }
      val byteCount = i * 1024
      val speed = byteCount * 2 / 1024d / 1024
      println(s"$hashFunction, ${provider.getName}: $speed MiB/s, ${i * 1024} bytes in 500 ms")
    } catch {
      case _: NoSuchAlgorithmException =>
    }
  }
}
