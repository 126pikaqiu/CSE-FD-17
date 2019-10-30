#ifndef _FILSYS_H_
#define _FILSYS_H_

#include <stdint.h>

/**
 * This is the header file from Unix Version 6 that describes the superblock of
 * the file system.  It has been converted to use stdint.h to work on 32- and 64-
 * bit systems. See comment in inode.h. 
 */

/**
 * Definition of the unix super block.  The root super block is allocated and
 * read in iinit/alloc.c.  Subsequently a super block is allocated and read
 * with each mount (smount/sys3.c) and released with unmount (sumount/sys3.c).
 * A disk block is ripped off for storage.  See alloc.c for general alloc/free
 * routines for free list and I list.
 *
 */

struct filsys {
  uint16_t	s_isize;	// size in blocks of I list 文件系統的存儲inode表的block總數
  uint16_t	s_fsize;	// size in blocks of entire volume 文件系统的盘块数目
  uint16_t	s_nfree;	// number of in core free blocks (0-100) 空闲盘块号数目
  uint16_t	s_free[100];	// in core free blocks 空闲盘块号栈
  uint16_t	s_ninode;	// number of in core I nodes (0-100) 空闲磁盘inode数目
  uint16_t	s_inode[100];	// in core free I nodes 空闲磁盘inode号栈
  uint8_t	s_flock;	// lock during free list manipulation 空闲盘块编号栈的锁字段
  uint8_t   s_ilock;	// lock during I list manipulation 空闲盘块inode栈的锁字段
  uint8_t	s_fmod;		// super block modified flag 超级快修改标志
  uint8_t	s_ronly;	// mounted read-only flag
  uint16_t	s_time[2];	// current date of last update 修改时间
  uint16_t	pad[48];        // aligns struct filesys to be 512 bytes in size (the block size!) 对齐的字节
};

#endif 
