package core.imp;

import Error.ErrorCode;
import core.*;
import core.inter.Block;
import core.inter.BlockManager;
import core.inter.Id;
import utils.BlockHandleUtil;
import utils.FileIOUtil;

public class BlockImp implements Block {

    private Id id;

    private byte[] catchBytes;

    private BlockMeta blockMeta;

    void setBlockManager(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    private BlockManager blockManager;

    @Override
    public Id getIndexId() {
        return id;
    }

    @Override
    public BlockManager getBlockManager() {
        return blockManager;
    }

    @Override
    public byte[] read() {
        //use the catch bytes
        if (this.catchBytes != null) {
            return catchBytes;
        }
        return FileIOUtil.readFromfile(blockManager.toString(),id.toString() + ".data");
    }

    @Override
    public int blockSize() {
        return blockMeta.getSize();
    }

    BlockImp(Id id, Id bmId) {
        this.id = id;
        this.blockManager =  new BlockManagerImp(bmId);
    }

    BlockImp(Id id) {
        this.id = id;
    }

    boolean createMeta(byte[] bs, int blockSize) {
        if (FileIOUtil.fileExists(blockManager.toString(), id.toString() + ".meta")) {
            return false;
        }
        this.blockMeta = new BlockMeta(blockSize, BlockHandleUtil.checkSum(bs));
        FileIOUtil.write2file(blockManager.toString(), id.toString() + ".meta",
                blockMeta.toString().getBytes());
        return true;
    }

    private boolean loadBlockMeta() {
        if (!FileIOUtil.fileExists(blockManager.toString(), id.toString() + ".meta")) {
            return false;
        }
        byte[] bs = FileIOUtil.readFromfile(blockManager.toString(), id.toString() + ".meta");
        String meta = new String(bs);
        this.blockMeta = BlockMeta.parseBlockMeta(meta);
        return true;
    }

    boolean writeData(byte[] bs) {
        if (FileIOUtil.fileExists(blockManager.toString(), id.toString() + ".data")) {
            return false;
        }
        FileIOUtil.write2file(blockManager.toString(), id.toString() + ".data", bs);
        return true;
    }

    public boolean exists() {
        return FileIOUtil.fileExists(blockManager.getId().toString(), id.toString() + ".data") &&
                FileIOUtil.fileExists(blockManager.getId().toString(), id.toString() + ".meta");
    }

    /**
     * to check whether the block is valid
     * @return valid or invalid
     */
    public boolean valid() {
        if (!exists()) {
            return false;
        }
        if(!loadBlockMeta()){
            // load the block meta to get the size and the checksum
            throw new ErrorCode(ErrorCode.CANNOT_FIND_BLOCK);
        }
        this.catchBytes = FileIOUtil.readFromfile(blockManager.toString(),id.toString() + ".data");
        return this.blockMeta.getCheckSum() == BlockHandleUtil.checkSum(this.catchBytes);
    }

    @Override
    public LogicBlock toLogicBlock() {
        return new LogicBlock(blockManager.getId(),id);
    }

}
