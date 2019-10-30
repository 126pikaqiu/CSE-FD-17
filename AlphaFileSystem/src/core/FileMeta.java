package core;

import AlphaFileSystem.core.imp.IntegerId;
import AlphaFileSystem.core.imp.StringId;

import java.util.ArrayList;
import java.util.Arrays;

public class FileMeta {
    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public ArrayList<ArrayList<LogicBlock>> getLogicBlocks() {
        return logicBlocks;
    }

    public void setLogicBlocks(ArrayList<ArrayList<LogicBlock>> logicBlocks) {
        this.logicBlocks = logicBlocks;
    }

    private long fileSize;

    private int blockSize;

    private ArrayList<ArrayList<LogicBlock>> logicBlocks;

    public FileMeta(long fileSize, int blockSize, ArrayList<ArrayList<LogicBlock>> logicBlocks){
        this.fileSize = fileSize;
        this.blockSize = blockSize;
        this.logicBlocks = logicBlocks;
    }

    public static FileMeta parseFileMeta(String str) {
        String[] lines = str.split("\n");
        long fileSize = Long.parseLong(lines[0].trim().split(":")[1]);
        int blockSize = Integer.parseInt(lines[1].trim().split(":")[1]);
        ArrayList<ArrayList<LogicBlock>> meta = new ArrayList<>();
        for(int i = 3; i < lines.length; i++) {
            if(lines[i].split(":").length == 1) {
                meta.add(null);//文件空洞需要填入空
                continue;
            }
            String[] logisBlockStr = lines[i].split(":")[1].trim().split(";");
            ArrayList<LogicBlock> logicBlocks = new ArrayList<>();
            Arrays.stream(logisBlockStr).forEach(e->{
                    StringId managerId = StringId.newId(e.split(",")[0].substring(1));
                    IntegerId blockId = IntegerId.newId(Long.parseLong(e.replace("]",
                            "").split(",")[1]));
                    logicBlocks.add(new LogicBlock(managerId,blockId));
            });
            meta.add(logicBlocks);
        }
        return new FileMeta(fileSize,blockSize,meta);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size:")
                .append(fileSize)
                .append("\nblock size:")
                .append(blockSize)
                .append("\nlogic block:");
        for(int i = 0; i < logicBlocks.size(); i++) {
            stringBuilder.append("\n").append(i).append(":");
            if(logicBlocks.get(i) != null) {
                logicBlocks.get(i).forEach(e -> {
                    stringBuilder.append(e.toString()).append(";");
                    //跳过空洞文件
                });
            }
        }
        return stringBuilder.toString();
    }
}
