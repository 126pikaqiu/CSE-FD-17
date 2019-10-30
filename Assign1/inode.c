#include <stdio.h>
#include <assert.h>

#include "inode.h"
#include "diskimg.h"


/**
 * Fetches the specified inode from the filesystem. 
 * Returns 0 on success, -1 on error.  
 */
int inode_iget(struct unixfilesystem *fs, int inumber, struct inode *inp) {
    if(inumber <= 0) {
        fprintf(stderr, "Inode number error\n");
        return -1;
    }
    int index_block = (inumber - 1) / 16 + INODE_START_SECTOR; // 一個sector可以存儲16個inode
    uint16_t bootblock[256];
    if (diskimg_readsector(fs->dfd, index_block, bootblock) != DISKIMG_SECTOR_SIZE) {
        fprintf(stderr, "Error reading inode table\n");
        return -1;
    }
    if (inp == NULL) {
        fprintf(stderr,"Null inode pointer.\n");
        return -1;

    }
    if(memcpy(inp, bootblock + ((inumber - 1) % 16) * 16, sizeof(struct inode)) !=(void *)inp) {
        fprintf(stderr, "Error memory mapping.\n");
        return -1;
    }
    return 0;
}


/**
 * Given an index of a file block, retrieves the file's actual block number
 * of from the given inode.
 *
 * Returns the disk block number on success, -1 on error.  
 */
int inode_indexlookup(struct unixfilesystem *fs, struct inode *inp, int blockNum) {
    if(inp->i_mode&ILARG) {
         // large file. Num - 1 indirect and 1 doubly-indirect
        // The block is referenced in the indirect block.
        int block_index_one_sector = blockNum / 256; // 0ne indirect block holds 256 block number.
        if(block_index_one_sector <= 6) {
            uint16_t blockNums[256];
            if (diskimg_readsector(fs->dfd, inp->i_addr[block_index_one_sector], blockNums) != DISKIMG_SECTOR_SIZE) {
                fprintf(stderr, "Error reading indirect block\n");
                return -1;
            }
            return blockNums[blockNum % 256];
        } else {
            uint16_t blockNums[256];
            if (diskimg_readsector(fs->dfd, inp->i_addr[7], blockNums) != DISKIMG_SECTOR_SIZE) {
                fprintf(stderr, "Error reading indirect block\n");
                return -1;
            }
            uint16_t blockNums1[256];
            if (diskimg_readsector(fs->dfd, blockNums[block_index_one_sector - 7], blockNums1) != DISKIMG_SECTOR_SIZE) {
                fprintf(stderr, "Error reading indirect block\n");
                return -1;
            }
            return blockNums1[blockNum % 256];
        }
    } else {// small file
        if(blockNum >= 8) {
            fprintf(stderr,"block out of bound.\n");
            return -1;
        }
        return inp->i_addr[blockNum];
    }
}


/**
 * Computes the size in bytes of the file identified by the given inode
 */
int inode_getsize(struct inode *inp) {
    return ((inp->i_size0 << 16) | inp->i_size1);
}
