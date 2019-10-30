
#include "pathname.h"
#include "directory.h"
#include "inode.h"
#include "diskimg.h"
#include <stdio.h>
#include <string.h>
#include <assert.h>


/**
 * Returns the inode number associated with the specified pathname.  This need only
 * handle absolute paths.  Returns a negative number (-1 is fine) if an error is 
 * encountered.
 */
int pathname_lookup(struct unixfilesystem *fs, const char *pathname) {
    if(pathname[0] != '/') {
        fprintf(stderr,"The path '%s' isn't an absolute path.\n", pathname);
        return -1;
    }
    if(pathname[1] == '\0') { // only the root
        return ROOT_INUMBER;
    }
    int next=1;  // the start index of the parsed path
    char pn[14];
    struct direntv6 dirEntTmp;
    int dirinumber = ROOT_INUMBER; // from the root to find the the file of a specified name
    while ((next=next_entry_name(pathname,pn,next)) >= 0) {
        int err = directory_findname(fs,pn,dirinumber,&dirEntTmp);
        if(err < 0) {
            fprintf(stderr, "Cannot find the dir entries of path '%s'.\n",pn);
            return -1;
        }
        dirinumber = dirEntTmp.d_inumber;
    }
    return dirEntTmp.d_inumber;
}

/**
 *  split the path and get the dir entry name.
 *  if the current entry is still a directory,
 *  return the start index of the next entry name,
 *  0 for others.
 */
int next_entry_name(char* pathname,char *entryname, int start) {
    if(start==0) {
        return -1;
    }
    int i;
    for(i = start; pathname[i] != ' ' && pathname[i] != '\0'; i++) {
        if(pathname[i] == '/') {
            break;
        }
        entryname[i-start] = pathname[i];
    }
    entryname[i-start] = '\0';
    return pathname[i]=='/'?(i + 1):0;
}
