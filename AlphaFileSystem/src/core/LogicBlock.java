package core;

import core.imp.IntegerId;
import core.imp.StringId;
import core.inter.Id;

public class LogicBlock {

    public Id getBlockManagerId() {
        return blockManagerId;
    }

    public void setBlockManagerId(StringId blockManagerId) {
        this.blockManagerId = blockManagerId;
    }

    public Id getBlockID() {
        return blockID;
    }

    public void setBlockID(IntegerId blockID) {
        this.blockID = blockID;
    }

    private Id blockManagerId;
    private Id blockID;

    public LogicBlock(Id blockManagerId, Id blockID) {
        this.blockManagerId = blockManagerId;
        this.blockID = blockID;
    }

    public String toString() {
        return "[" +
                blockManagerId.toString() +
                "," +
                blockID.toString() +
                "]";
    }
}
