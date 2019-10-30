#include <stdio.h>
#include <stdlib.h>
#include "unixfilesystem.h"
#include "diskimg.h" 

/**
 * Allocates and initializes a struct unixfilesystem given a filedescriptor to 
 * an open disk image. 
 * Return NULL on error. 
 */

struct unixfilesystem *unixfilesystem_init(int dfd) {
  // Validate the bootblock.  This will catch the situation where something 
  // other than a descriptor to a valid diskimg is passed in.
  uint16_t bootblock[256]; //16位的整数，一个整数存储2字节，所以256存储512字节，一个sector
  //读取启动块，获得启动块的长度
  if (diskimg_readsector(dfd, BOOTBLOCK_SECTOR, bootblock) != DISKIMG_SECTOR_SIZE) {
    fprintf(stderr, "Error reading bootblock\n");
    return NULL;
  }

  //魔术字节不对
  if (bootblock[0] != BOOTBLOCK_MAGIC_NUM) {
    fprintf(stderr, "Bad magic number on disk(0x%x)\n", bootblock[0]);
    return NULL;
  }

  //超级块结构的定义是否有问题
  if (sizeof(struct filsys) != DISKIMG_SECTOR_SIZE) { 
    fprintf(stderr, "Warning: Superblock structure size (%zu) != SECTOR_SIZE\n",
            sizeof(struct filsys));
  }

  //分配空间
  struct unixfilesystem *fs = malloc(sizeof(struct unixfilesystem));
  if (fs == NULL) {
    fprintf(stderr,"Out of memory.\n");
    return NULL;
  }
  //传入文件描述符
  fs->dfd = dfd;
  // 通过文件描述符，读取超级块，第二参数个是超级快对应
  // 的块索引1，第三个参数是超级块存储的区域
  if (diskimg_readsector(dfd, SUPERBLOCK_SECTOR, &fs->superblock) != DISKIMG_SECTOR_SIZE) {
    fprintf(stderr, "Error reading superblock\n");
    free(fs);
    return NULL;
  }

  return fs;
}
