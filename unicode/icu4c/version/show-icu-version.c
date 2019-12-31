#include <unicode/uchar.h>
#include <stdio.h>

int main()
{
  UVersionInfo version_info;
  u_getUnicodeVersion(version_info);
  printf(
      "u_getUnicodeVersion => %d.%d.%d.%d\n",
      version_info[0],
      version_info[1],
      version_info[2],
      version_info[3]);
  return 0;
}

