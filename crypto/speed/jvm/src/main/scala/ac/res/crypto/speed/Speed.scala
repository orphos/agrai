// Copyright 2019 Takezoe,Tomoaki <tomoaki3478@res.ac>
//
// SPDX-License-Identifier: Apache-2.0 WITH LLVM-exception
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// As an exception, if, as a result of your compiling your source code, portions
// of this Software are embedded into an Object form of such source code, you
// may redistribute such embedded portions in such Object form without complying
// with the conditions of Sections 4(a), 4(b) and 4(d) of the License.
//
// In addition, if you combine or link compiled forms of this Software with
// software that is licensed under the GPLv2 ("Combined Software") and if a
// court of competent jurisdiction determines that the patent provision (Section
// 3), the indemnity provision (Section 9) or other Section of the License
// conflicts with the conditions of the GPLv2, you may retroactively and
// prospectively choose to deem waived or otherwise exclude such Section(s) of
// the License, but only in their entirety and only with respect to the Combined
// Software.

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
