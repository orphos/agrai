#include <sys/types.h>
#include <unistd.h>
#include <stdint.h>

int64_t native_getpid()
{
  return (int64_t)getpid();
}

