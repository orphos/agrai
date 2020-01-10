// Copyright 2020 Takezoe,Tomoaki <tomoaki3478@res.ac>
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

#include <jni.h>
#include <fenv.h>
#include <math.h>

#ifdef __amd64__
#include <emmintrin.h>
#endif

static const unsigned int mxcsr_precision_mask = 1 << 5;

JNIEXPORT jdouble JNICALL Java_ac_res_jvm_benchmark_JNI_addDoubleExact(JNIEnv *jenv, jdouble left, jdouble right)
{
#ifdef __amd64__
  _mm_setcsr(_mm_getcsr() & ~mxcsr_precision_mask);
#else
  feclearexcept(FE_ALL_EXCEPT);
#endif
  double ret = (double)left + (double)right;
#ifdef __amd64__
  int inexact = _mm_getcsr() & mxcsr_precision_mask;
#else
  int inexact = fetestexcept(FE_INEXACT);
#endif
  return (jdouble)(inexact ? NAN : ret);
}
