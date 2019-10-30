#include "directory.h"
#include "inode.h"
#include "diskimg.h"
#include "file.h"
#include <stdio.h>
#include <string.h>
#include <assert.h>

/**
 * Looks up the specified name (name) in the specified directory (dirinumber).  
 * If found, return the directory entry in space addressed by dirEnt.  Returns 0 
 * on success and something negative on failure. 
 */
int directory_findname(struct unixfilesystem *fs, const char *name,
        int dirinumber, struct direntv6 *dirEnt) {
    struct inode in;
    int err = inode_iget(fs,dirinumber,&in);
    if(err < 0 ) {
        fprintf(stderr, "Cannot find the inode of inumber %d\n",dirinumber);
        return err;
    }
    if (!(in.i_mode & IALLOC)) {
        fprintf(stderr, "The inode of inumber %d isn't allocated.\n",dirinumber);
        // The inode isn't allocated, so we must find an error one.
        return -1;
    }
    int size = inode_getsize(&in);
    for (int offset = 0; offset < size; offset += DISKIMG_SECTOR_SIZE) {
        char buf[DISKIMG_SECTOR_SIZE];
        int bno = offset/DISKIMG_SECTOR_SIZE;
        int bytesMoved = file_getblock(fs, dirinumber, bno, buf);
        if (bytesMoved < 0)
            return -1;
        struct direntv6 dirEntTmp;
        for(int i=0; i < bytesMoved; i += 16) {
            memcpy(&dirEntTmp, buf + i, sizeof(struct direntv6));
            if(cmp_path(&(dirEntTmp.d_name),name)){
                memcpy(dirEnt, &dirEntTmp, sizeof(struct direntv6));
                return 0; // match the specified dir name.
            }
        }
    }
    // not found the dir entries of the specified name.
    return -2;
}

int cmp_path(const char* str1, const char* str2){
    for(int i = 0; i < 14; i++) {
        if(str1[i] != str2[i]) {
            return 0;
        }else if(str1[i]=='\0'){ // 字符串结尾，提前结束，防止内存读取异常
            break;
        }
    }
    return 1;
}

