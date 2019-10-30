package core.imp;

import Error.ErrorCode;
import core.inter.Block;
import core.inter.BlockManager;
import core.inter.Id;

public class BlockManagerImp implements BlockManager {

    public static int defauleBlockSize = 512;

    public Id getId() {
        return id;
    }

    public void setId(StringId id) {
        this.id = id;
    }

    private Id id;

    @Override
    public Block getBlock(Id indexId) {
        if (!(indexId instanceof  IntegerId) ) {
            throw new ErrorCode(ErrorCode.ID_TYPE_ERROR);
        }
        BlockImp block = new BlockImp(indexId);
        block.setBlockManager(this);
        return block;
    }

    @Override
    public Block newBlock(byte[] b) {
        IntegerId blId = IntegerId.newId();
        BlockImp block = new BlockImp(blId);
        block.setBlockManager(this);
        if(!block.createMeta(b, defauleBlockSize) || !block.writeData(b)) {
            // 创建元数据和物理块
            throw new ErrorCode(ErrorCode.NEW_BLOCK_ERROR);
        }
        return block;
    }

    public BlockManagerImp(Id id) {
        this.id = id;
    }

    public String toString() {
        return id.toString();
    }

}
