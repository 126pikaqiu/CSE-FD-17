package core.inter;

public interface FileManager {
    File getFile(Id fileId);
    File newFile(Id fileId);
    Id getId();
}
