# sbt-native-example
This project shows how to create SBT project that compiles native library using
Meson and Ninja as a resource generator, and Scala code that copies shared
library into temporary directory at runtime, loads it and calls native function
using JNR.
