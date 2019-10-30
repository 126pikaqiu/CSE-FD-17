package core;

public class BlockMeta {
    private int size = 512;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(long checkSum) {
        this.checkSum = checkSum;
    }

    public BlockMeta(int size, long checkSum) {
        this.size = size;
        this.checkSum = checkSum;
    }

    private long checkSum;

    public static BlockMeta parseBlockMeta(String meta) {
        String sizeLine = meta.split("\n")[0];
        String checkSumLine = meta.split("\n")[1];
        int size = Integer.parseInt(sizeLine.split(":")[1].trim());
        long checkSum = Long.parseLong(checkSumLine.trim().split(":")[1].trim());
        return new BlockMeta(size,checkSum);
    }

    public String toString() {
        return "size: " + size + "\nchecksum: " + checkSum;
    }

}
