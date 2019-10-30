package core.inter;

import core.LogicBlock;

public interface Block {
    Id getIndexId();
    BlockManager getBlockManager();
    byte[] read();
    int blockSize();
    boolean exists();
    boolean valid();
    LogicBlock toLogicBlock();
}
