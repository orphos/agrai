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
