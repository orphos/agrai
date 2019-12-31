#include <dlfcn.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

typedef void (*get_version)(uint8_t [4]);

bool run(const char *filename)
{
  void *lib = dlopen(filename, RTLD_NOW);
  if (lib == NULL)
    return false;
  get_version get = (get_version)dlsym(lib, "u_getUnicodeVersion");
  if (get == NULL)
    get = (get_version)dlsym(lib, "u_getUnicodeVersion_64");
  if (get == NULL)
    return false;
  uint8_t version_info[4];
  get(version_info);
  printf(
      "%s::u_getUnicodeVersion => %d.%d.%d\n",
      filename,
      version_info[0],
      version_info[1],
      version_info[2]);
  return true;
}

int main()
{
  bool ret = false;
#ifdef __APPLE__
  ret |= run("libicucore.dylib");
  ret |= run("libicucore.A.dylib");
  ret |= run("libicuuc.dylib");
  ret |= run("libicuuc.64.dylib");
#endif
  return ret ? 0 : 1;
}

