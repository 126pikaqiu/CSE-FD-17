package core.imp;

import Error.ErrorCode;
import core.*;
import core.inter.Block;
import core.inter.File;
import core.inter.FileManager;
import core.inter.Id;
import utils.BlockHandleUtil;
import utils.FileIOUtil;
import java.util.ArrayList;

public class FileImp implements File {

    public static final int MOVE_CURR = 0;
    public static final int MOVE_HEAD = 1;
    public static final int MOVE_TAIL = 2;
    private long cursor;
    private Id fileId;

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    private FileManager fileManager;

    public FileMeta getFileMeta() {
        if(fileMeta==null) {
            loadFileMeta();
        }
        return fileMeta;
    }

    public void setFileMeta(FileMeta fileMeta) {
        this.fileMeta = fileMeta;
        writeMeta();
    }

    private FileMeta fileMeta;

    @Override
    public Id getFileId() {
        return fileId;
    }

    FileImp(Id fileId, Id fmId) {
        this.fileId = fileId;
        this.fileManager = new FileManagerImp(fmId);
        this.cursor = 0; // 创建file时先将游标归零，表示已经读取或者写的字节数
    }

    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public byte[] read(int length) {
        if(!loadFileMeta()) {
            // load the file meta once read
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        if (cursor >= fileMeta.getFileSize()) { // 游标已经超过了文件的大小时返回空数组，游标从0开始
            return null;
        }
        // 实际读取到的字节数
        int accessLength = cursor + length > fileMeta.getFileSize()? (int) (fileMeta.getFileSize() - cursor):length;
        int startBlock = (int) (cursor / fileMeta.getBlockSize()); //开始逻辑块序号
        int endBlock = (int) ((accessLength + cursor - 1) / fileMeta.getBlockSize()); //结束逻辑块序号
        byte[] ret = new byte[accessLength];
        long oldCursor = cursor;
        int retStart = 0;
        for(int i = startBlock; i <= endBlock; i++) {
            int len = (oldCursor + accessLength - i * fileMeta.getBlockSize()) > fileMeta.getBlockSize()?fileMeta.getBlockSize():
                    (int) (oldCursor + accessLength - i * fileMeta.getBlockSize());
            int bkStart = (int) (cursor % fileMeta.getBlockSize());
            byte[] bs;
            // 返回null表示同一逻辑块对应的所有副本皆不可用
            if((bs= readFromLogictBlocks(fileMeta.getLogicBlocks().get(i), bkStart, len)) == null) {
                //拷贝失败则文件损坏了
                throw new ErrorCode(ErrorCode.FILE_DAMAGED);
            }
            cursor += len; // 移动游标
            System.arraycopy(bs,0,ret, retStart, len); //拷贝出来当前逻辑块需要读取的字节
            retStart += len;

        }
        return ret;
    }

    @Override
    public void write(byte[] b) {
        if(!loadFileMeta()) {
            // load the file meta once read
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        if(b==null) {
            throw new ErrorCode(ErrorCode.WRITE_NULL_ERROR);
        }
        int length = b.length;
        ArrayList<ArrayList<LogicBlock>> lists = fileMeta.getLogicBlocks();
        int startBlock = (int) (cursor / fileMeta.getBlockSize()); //开始逻辑块序号
        int endBlock = (int) ((b.length + cursor - 1) / fileMeta.getBlockSize()); //结束逻辑块序号

        b = completionByte(b,lists,startBlock,endBlock);

        //存在文件空洞则将逻辑块拉长startBlock - lists.size()
        if(startBlock > lists.size()) {
            BlockHandleUtil.stretch(lists, startBlock - lists.size());
        }
        assert b != null;
        for(int i = startBlock; i <= endBlock; i++) {
            int dup = (int) (Math.random() * 3 + 1);  // 副本数量1至3
            byte[] bs = new byte[fileMeta.getBlockSize()];
            System.arraycopy(b, (i - startBlock) * fileMeta.getBlockSize(), bs, 0, fileMeta.getBlockSize());
            if(lists.size() <= i)
                lists.add(BlockHandleUtil.newLogicBlocks(dup,bs));
            else
                lists.set(i,BlockHandleUtil.newLogicBlocks(dup,bs));
        }
        long newFileSize = Math.max(cursor + length, fileMeta.getFileSize());
        cursor += length;
        fileMeta.setFileSize(newFileSize);
        fileMeta.setLogicBlocks(lists);
        writeMeta();
    }

    @Override
    public long move(long offset, int where) {

        if(!loadFileMeta()) {
            // load the file meta once read
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        if(offset < 0) {
            throw new ErrorCode(ErrorCode.FILE_CURSOR_MOVE_ERROR);
        }
        switch (where) {
            case MOVE_HEAD:
                cursor = offset; break;
            case MOVE_TAIL:
                cursor = fileMeta.getFileSize() + offset;break;
            case MOVE_CURR:
                cursor = cursor + offset; break;
            default:
                throw new ErrorCode(ErrorCode.FILE_CURSOR_MOVE_ERROR);

        }
        return offset;
    }

    @Override
    public void close() {
        fileMeta = null;
        fileManager = null;
        fileId = null;
    }

    @Override
    public long size() {
        if(!loadFileMeta()) {
            // load the file meta once read
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        return fileMeta.getFileSize();
    }

    @Override
    public void setSize(long newSize) {
        if(!loadFileMeta()) {
            // load the file meta once read
            throw new ErrorCode(ErrorCode.CANNOT_FIND_FILE);
        }
        ArrayList<ArrayList<LogicBlock>> oldLists = fileMeta.getLogicBlocks();
        ArrayList<ArrayList<LogicBlock>> newLists = new ArrayList<>();
        int logicBlockNumber = (int) ((newSize - 1) / fileMeta.getBlockSize() + 1);
        if (newSize > 0 && newSize >= fileMeta.getFileSize()) {
            if(logicBlockNumber > oldLists.size())
            // 文件大小变大，并且出现文件空洞则直接拉长
            BlockHandleUtil.stretch(oldLists, logicBlockNumber - oldLists.size());
            newLists = oldLists;
        } else if(newSize > 0){
            // 拷贝出最后一块逻辑块，前面的值保留
            for(int i = 0; i < logicBlockNumber - 1; i++) {
                newLists.add(oldLists.get(i));
            }
            int len = (int) (newSize - (logicBlockNumber - 1) * fileMeta.getBlockSize()); // 需要拷贝的字节长度
            if (len > 0) {
                byte[] bs;
                // 返回null表示同一逻辑块对应的所有副本皆不可用
                if((bs= readFromLogictBlocks(oldLists.get(logicBlockNumber - 1), 0, len)) == null) {
                    //拷贝失败则文件损坏了
                    throw new ErrorCode(ErrorCode.FILE_DAMAGED);
                }
                // 拷贝3个副本
                ArrayList<LogicBlock> logicBlocks = BlockHandleUtil.newLogicBlocks(3,bs);
                newLists.add(logicBlocks);
            }
        }
        fileMeta.setLogicBlocks(newLists);
        fileMeta.setFileSize(newSize);
        writeMeta();
    }

    @Override
    public boolean exists() {
        return FileIOUtil.fileExists(fileManager.getId().toString(), fileId.toString() + ".meta");
    }

    private boolean loadFileMeta() {
        if (!FileIOUtil.fileExists(fileManager.getId().toString(), fileId.toString() + ".meta")) {
            return false;
        }
        if (fileMeta != null) {
            return true;
        }
        byte[] metaBytes = FileIOUtil.readFromfile(fileManager.getId().toString(), fileId.toString() + ".meta");
        String fileMetaStr = new String(metaBytes);
        this.fileMeta = FileMeta.parseFileMeta(fileMetaStr);
        return true;
    }

    public boolean writeMeta() {
        FileIOUtil.write2file(fileManager.getId().toString(), fileId.toString() + ".meta",fileMeta.toString().getBytes());
        return true;
    }

    private byte[] readFromLogictBlocks(ArrayList<LogicBlock> logicBlocks, int bkstart, int length) {
        byte[] ret = new byte[length];
        if(logicBlocks == null) { //文件空洞则直接返回
            return ret;
        }
        // 遍历所有物理块副本，一旦可用就读取，跳出遍历
        for (LogicBlock logicBlock : logicBlocks) {
            Block block = new BlockImp(logicBlock.getBlockID(), logicBlock.getBlockManagerId());
            if (block.valid()) {
                byte[] blbs = block.read();
                for (int j = 0; bkstart + j < blbs.length && j < length; j++) {
                    ret[j] = blbs[bkstart + j];
                }
                return ret;
            }
        }
        return null;
    }

    private byte[] completionByte(byte[] bs, ArrayList<ArrayList<LogicBlock>> lists, int start, int end) {
        byte[] ret = new byte[(end - start + 1)* fileMeta.getBlockSize()];
        byte[] catchBytes;
        int len = (int) (cursor % fileMeta.getBlockSize());
        if(start < lists.size() && len > 0) { // 需要拷贝出第一个逻辑块的部分内容
            if((catchBytes = readFromLogictBlocks(lists.get(start),0, 512)) == null) {
                //拷贝失败则文件损坏了
                throw new ErrorCode(ErrorCode.FILE_DAMAGED);
            }
            System.arraycopy(catchBytes,0,ret,0,len);
        }
        System.arraycopy(bs,0, ret, len, bs.length); //装入需要写入的字节

        //需要拷贝出的最后一个逻辑块的字节长度
        int length = fileMeta.getBlockSize() - (int) ((cursor + bs.length) % fileMeta.getBlockSize());
        if(start < end && end < lists.size() && length > 0) { // 需要拷贝出最后一个逻辑块的部分内容
            if((catchBytes = readFromLogictBlocks(lists.get(end),0, 512)) == null) {
                //拷贝失败则文件损坏了
                throw new ErrorCode(ErrorCode.FILE_DAMAGED);
            }
            System.arraycopy(catchBytes,fileMeta.getBlockSize() - length,ret,len + bs.length,length);
        }
        return ret;
    }

    public boolean createFile() {
        if(exists()) {
            return false;
        }
        ArrayList<ArrayList<LogicBlock>> blank = new ArrayList<>();
        this.fileMeta = new FileMeta(0, BlockManagerImp.defauleBlockSize, blank);
        return writeMeta();
    }
}
