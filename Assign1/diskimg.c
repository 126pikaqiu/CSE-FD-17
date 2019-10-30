#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include "diskimg.h"

int diskimg_open(char *pathname, int readOnly) {
    //打开文件，返回文件描述符
  return open(pathname, readOnly ? O_RDONLY : O_RDWR);
}

int diskimg_getsize(int fd) {
    //lseek 移动游标，移动到文件末尾+偏移量
  return lseek(fd, 0, SEEK_END);
}

int diskimg_readsector(int fd, int sectorNum,  void *buf) {
    // 先判断第sectorNum对应的sector是否存在（这个过程涉及到移动游标），存在则读取相应的sector，
    //返回的的实际读取到的字节数量
  if (lseek(fd, sectorNum * DISKIMG_SECTOR_SIZE, SEEK_SET) == (off_t) -1) return -1;  
  return read(fd, buf, DISKIMG_SECTOR_SIZE);
}

int diskimg_writesector(int fd, int sectorNum,  void *buf) {
    // 先判断第sectorNum对应的sector是否存在（这个过程涉及到移动游标），
    // 存在则写入相应的sector，返回的是实际修改的字节数量
  if (lseek(fd, sectorNum * DISKIMG_SECTOR_SIZE, SEEK_SET) == (off_t) -1) {
    return -1;
  }

  return write(fd, buf, DISKIMG_SECTOR_SIZE);
}

int diskimg_close(int fd) {
  return close(fd);
}
