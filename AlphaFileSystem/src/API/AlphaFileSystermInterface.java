package API;

import Error.ErrorCode;
import core.imp.BlockManagerImp;
import core.imp.FileManagerImp;
import core.imp.IntegerId;
import core.imp.StringId;
import core.inter.Block;
import core.inter.BlockManager;
import core.inter.File;
import core.inter.FileManager;
import utils.BlockHandleUtil;

public class AlphaFileSystermInterface {

    public static void cat(String name) {
        String fmId = name.split(",")[0];
        String fileId = name.split(",")[1];
        FileManager fileManager = new FileManagerImp(StringId.newId(fmId));
        File file = fileManager.getFile(StringId.newId(fileId));
        if(file.exists()) {
            byte[] buffer;
            int bufferSize = 1024;
            while ((buffer=file.read(bufferSize)) != null) {
                System.out.print(new String(buffer));
            }

        } else {
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        file.close();
    }


    public static void copy(String src, String tar,int type) {
        String fmId1 = src.split(",")[0];
        String fileId1 = src.split(",")[1];
        String fileId2 = tar.split(",")[1];
        String fmId2 = tar.split(",")[0];
        FileManager fileManager1 = new FileManagerImp(StringId.newId(fmId1));
        FileManager fileManager2 = new FileManagerImp(StringId.newId(fmId2));
        File file2 = fileManager2.newFile(StringId.newId(fileId2));
        File file1 = fileManager1.getFile(StringId.newId(fileId1));
        if(file1.exists()) {
            if(type==1){
                //方法一
                file2.setFileMeta(file1.getFileMeta());
            } else {
                //方法二
                byte[] buffer;
                int bufferSize = 1024;
                while ((buffer=file1.read(bufferSize)) != null) {
                    file2.write(buffer);
                }
            }


        } else {
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        file1.close();
        file2.close();
    }

    public static void write(String src, byte[] bs, long offset, int whence) {
        String fileId = src.split(",")[1];
        String fmId = src.split(",")[0];
        FileManager fileManager = new FileManagerImp(StringId.newId(fmId));
        File file = fileManager.getFile(StringId.newId(fileId));
        if(!file.exists()) {
            file.createFile();
        }
        file.move(offset,whence);
        file.write(bs);
        file.close();
    }

    public static void hex(String name) {
        String bmId = name.split(",")[0];
        Long indexId = Long.parseLong(name.split(",")[1]);
        BlockManager blockManager = new BlockManagerImp(StringId.newId(bmId));
        Block block = blockManager.getBlock(IntegerId.newId(indexId));
        if(block.exists()) {
            byte[] bs = block.read();
            for(int i = 0; i < bs.length; i++) {
                if(i % 16 == 0) {
                    System.out.println();
                }
                System.out.print(BlockHandleUtil.byte2hex(bs[i]));
            }
        } else {
            throw new ErrorCode(ErrorCode.CANNOT_FIND_BLOCK);
        }
    }

}
