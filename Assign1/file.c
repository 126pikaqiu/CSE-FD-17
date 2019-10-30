#include <stdio.h>
#include <assert.h>

#include "file.h"
#include "inode.h"
#include "diskimg.h"

/**
 * Fetches the specified file block from the specified inode.
 * Returns the number of valid bytes in the block, -1 on error.
 */
int file_getblock(struct unixfilesystem *fs, int inumber, int blockNum, void *buf) {
    struct inode in;
    if(inode_iget(fs,inumber,&in) < 0) {
        fprintf(stderr, "Cannot find inode of inumber %s.\n",inumber);
        return -1;
    }
    int index_block =  inode_indexlookup(fs,&in,blockNum);
    if(index_block  < 0 ) {
        fprintf(stderr, "Cannot find block %s of inode %s.\n",blockNum, inumber);
        return -1;
    }
    int err = diskimg_readsector(fs->dfd, index_block, buf);
    if(err < 0) {
        fprintf(stderr, "Error reading block %d.\n",index_block);
        return -1;
    }
    int size = inode_getsize(&in) - blockNum * DISKIMG_SECTOR_SIZE;
    return size < DISKIMG_SECTOR_SIZE ? size: DISKIMG_SECTOR_SIZE;
}
