package co.matisses.webintegrator.dto;

/**
 *
 * @author dbotero
 */
public class MailAttachmentDTO {
    public static final String SOURCE_WEB = "WEB";
    public static final String SOURCE_FILE_SYSTEM = "FILE";
    private String fileName;
    private String fileURL;
    private String source;

    public MailAttachmentDTO() {
    }

    public MailAttachmentDTO(String fileName, String fileURL, String source) {
        this.fileName = fileName;
        this.fileURL = fileURL;
        this.source = source;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
