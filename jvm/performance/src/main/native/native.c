#include <jni.h>
#include <fenv.h>
#include <math.h>
#include <emmintrin.h>

JNIEXPORT jdouble JNICALL Java_ac_res_jvm_benchmark_JNI_addDoubleExact(JNIEnv *jenv, jdouble left, jdouble right)
{
#ifdef __amd64__
  _mm_setcsr(_mm_getcsr() ^ FE_INEXACT);
#else
  feclearexcept(FE_ALL_EXCEPT);
#endif
  double ret = (double)left + (double)right;
#ifdef __amd64__
  int inexact = _mm_getcsr() & FE_INEXACT;
#else
  int inexact = fetestexcept(FE_INEXACT);
#endif
  return (jdouble)(inexact ? NAN : ret);
}
