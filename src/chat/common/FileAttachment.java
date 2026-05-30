package common;

import java.io.Serializable;

public class FileAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private String contentType;
    private byte[] data;

    public FileAttachment(String fileName, String contentType, byte[] data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}
