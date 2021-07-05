package openbrfacerecogapp;

public class FolderContainer {

    private ImageContainer orginalImage;
    private ImageContainer detectedImage;
    private ImageContainer faceImage;

    private String xmlFileName;
    private String xmlFilePath;

    private String folderContainerName;
    private String folderContainerPath;


    public String getFolderContainerPath() {
        return folderContainerPath;
    }

    public void setFolderContainerPath(String folderContainerPath) {
        this.folderContainerPath = folderContainerPath;
    }


    public ImageContainer getOrginalImage() {
        return orginalImage;
    }

    public void setOrginalImage(ImageContainer orginalImage) {
        this.orginalImage = orginalImage;
    }

    public ImageContainer getDetectedImage() {
        return detectedImage;
    }

    public void setDetectedImage(ImageContainer detectedImage) {
        this.detectedImage = detectedImage;
    }

    public ImageContainer getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(ImageContainer faceImage) {
        this.faceImage = faceImage;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public String getFolderContainerName() {
        return folderContainerName;
    }

    public void setFolderContainerName(String folderContainerName) {
        this.folderContainerName = folderContainerName;
    }
}
