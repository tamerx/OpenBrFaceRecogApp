package openbrfacerecogapp;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CameraCaptureController implements Initializable {

    public static boolean isCapture = false; // For stop & resume thread of camera
    public static Webcam webcam;


    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    boolean isFaceInImage = false;

    Mat opencvWebCamImage;
    Image faceDetectedImage;
    Image capturedImage;
    Mat faceCvImage;
    Image faceImage;

    @FXML
    Button myReload;

    @FXML
    Button btnCapture;

    @FXML
    Button btnSave;

    @FXML
    Button btnScanFace;

    @FXML
    private AnchorPane imgContainer;

    @FXML
    private ImageView imageView;

    private FileChooser fileChooser; // For select path of saving picture captured
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;
    private String scanImagePath = "";
    private String scanImageFolderPath = "";
    private String scanImagePathToSave = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnScanFace.setStyle("-fx-background-color: #ab4816");
        btnScanFace.setDisable(true);

        /* Bind views */
        imageView.fitWidthProperty().bind(imgContainer.widthProperty());
        imageView.fitHeightProperty().bind(imgContainer.heightProperty());

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.faceCascade = new CascadeClassifier();

        this.faceCascade.load("C:/Users/Asus/IdeaProjects/OpenBrFaceRecogApp/src/main/resources/cascade/haarcascade_frontalface_alt.xml");

        System.out.println((getClass().getResource("/cascade/haarcascade_frontalface_alt.xml").getPath().substring(1)));

        this.absoluteFaceSize = 0;

        /* Init camera */
        webcam = Webcam.getDefault();


        if (webcam == null) {
            System.out.println("Camera not found !");
            System.exit(-1);
        }


        Dimension[] nonStandardResolutions = new Dimension[]{
                WebcamResolution.HD720.getSize(),
        };
        Webcam webcam = Webcam.getDefault();
        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(WebcamResolution.HD720.getSize());
        webcam.open();

//        webcam.setViewSize(WebcamResolution.VGA.getSize());
//        webcam.open();

        new VideoTacker().start(); // Start camera capture

        /* Init save file chooser */
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (*.png)", "*.png"));
        fileChooser.setTitle("Save Image");

    }


    @FXML
    private void capture() { // Stop camera & taking picture

        isCapture = true;


        BufferedImage webcamBufferedImage = webcam.getImage();
        opencvWebCamImage = detectFace(Utils.bufferedImage2Mat_v2(webcamBufferedImage));
        capturedImage = SwingFXUtils.toFXImage(webcamBufferedImage, null);

        faceImage = Utils.mat2Image(faceCvImage);
        faceDetectedImage = Utils.mat2Image(opencvWebCamImage);
        imageView.setImage(faceDetectedImage);

        if (isFaceInImage == false) {
            btnScanFace.setDisable(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bilgi");
            alert.setHeaderText(null);
            alert.setContentText("No Face Detected In Image");
            alert.showAndWait();
        } else if (isFaceInImage == true) {
            btnScanFace.setDisable(false);
        }

    }

    @FXML
    private void reload() { // Resume camera capture

        isCapture = false;
        btnScanFace.setDisable(true);
        new VideoTacker().start();
    }

    @FXML
    private void saveImage() {
        isCapture = true; // Stop taking pictures

        // File file = fileChooser.showSaveDialog(imageView.getScene().getWindow());
        String uniqueID = UUID.randomUUID().toString();
        String localImagePath = "C:\\Users\\Asus\\IdeaProjects\\OpenBrFaceRecogApp\\ImageFolders";

        String folderPath = localImagePath + "\\" + uniqueID;

        // local const variables
        String ORGINAL = "orginal_";
        String DETECTED = "detected_";
        String FACE = "face_";
        String XML = "xml_";
        String FOLDER = "folder_";
        String IMAGE_EXTENSION = ".jpg";
        String XML_EXTENSION = ".xml";

        String xmlName = XML + uniqueID + XML_EXTENSION;
        String xmlPath = folderPath + "\\" + XML + uniqueID + XML_EXTENSION;

        String localMatchImagesPath = "C:\\Users\\Asus\\IdeaProjects\\OpenBrFaceRecogApp\\MatchImages";
        String localMatchImage = localMatchImagesPath + "\\" + ORGINAL + uniqueID + IMAGE_EXTENSION;

        scanImageFolderPath = localMatchImagesPath;


        String orginalImagePath = folderPath + "\\" + ORGINAL + uniqueID + IMAGE_EXTENSION;
        String orginalImageName = ORGINAL + uniqueID + IMAGE_EXTENSION;

        String imageFacePath = folderPath + "\\" + FACE + uniqueID + IMAGE_EXTENSION;
        String imageFaceName = FACE + uniqueID + IMAGE_EXTENSION;

        String detectedImagePath = folderPath + "\\" + DETECTED + uniqueID + IMAGE_EXTENSION;
        String detectedImageName = DETECTED + uniqueID + IMAGE_EXTENSION;

        scanImagePath = orginalImagePath;
        scanImagePathToSave = localMatchImage;

        File folder = new File(folderPath);
        File fileImage = new File(orginalImagePath);
        File fileFaceImage = new File(imageFacePath);
        File fileDetectedImage = new File(detectedImagePath);
        //File scanDetectedImage = new File(localMatchImage);

        FolderContainer folderContainer = new FolderContainer();


        folderContainer.setXmlFileName(xmlName);
        folderContainer.setXmlFilePath(xmlPath);
        folderContainer.setFolderContainerName(uniqueID);
        folderContainer.setFolderContainerPath(folderPath);

        ImageContainer orginalImageContainer = new ImageContainer();

        orginalImageContainer.setName(orginalImageName);
        orginalImageContainer.setPath(orginalImagePath);
        orginalImageContainer.setWidth((int) capturedImage.getWidth());
        orginalImageContainer.setHeight((int) capturedImage.getHeight());

        ImageContainer faceImageContainer = new ImageContainer();


        faceImageContainer.setName(imageFaceName);
        faceImageContainer.setPath(imageFacePath);
        faceImageContainer.setWidth((int) faceImage.getWidth());
        faceImageContainer.setHeight((int) faceImage.getHeight());

        ImageContainer detectedImageContainer = new ImageContainer();

        detectedImageContainer.setName(detectedImageName);
        detectedImageContainer.setPath(detectedImagePath);
        detectedImageContainer.setWidth((int) faceDetectedImage.getWidth());
        detectedImageContainer.setHeight((int) faceDetectedImage.getHeight());


        folderContainer.setOrginalImage(orginalImageContainer);
        folderContainer.setFaceImage(faceImageContainer);
        folderContainer.setDetectedImage(detectedImageContainer);


        boolean bool = folder.mkdir();
        if (bool) {

            if (fileImage != null && fileFaceImage != null && fileDetectedImage != null) {
                try { // Save picture with .png extension
                    ImageIO.write(SwingFXUtils.fromFXImage(capturedImage, null), "PNG", fileImage);
                    //ImageIO.write(SwingFXUtils.fromFXImage(capturedImage, null), "PNG", scanDetectedImage);
                    ImageIO.write(SwingFXUtils.fromFXImage(this.faceImage, null), "PNG", fileFaceImage);
                    ImageIO.write(SwingFXUtils.fromFXImage(faceDetectedImage, null), "PNG", fileDetectedImage);


                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                    // root elements
                    Document doc = docBuilder.newDocument();
                    Element rootElement = doc.createElement("xmlInfo");


                    // Orginal Image Node
                    Element orginalImage = doc.createElement(ORGINAL);
                    // add staff to root

                    // add xml attribute
                    orginalImage.setAttribute("id", "1001");

                    // alternative
                    // Attr attr = doc.createAttribute("id");
                    // attr.setValue("1001");
                    // staff.setAttributeNode(attr);

                    Element name = doc.createElement("name");
                    name.setTextContent(folderContainer.getOrginalImage().getName());
                    orginalImage.appendChild(name);

                    Element path = doc.createElement("path");
                    path.setTextContent(folderContainer.getOrginalImage().getPath());
                    orginalImage.appendChild(path);

                    Element size = doc.createElement("size");
                    size.setAttribute("w_h", "Width_Height");

                    Element width = doc.createElement("width");
                    width.setTextContent(Integer.toString(folderContainer.getOrginalImage().getWidth()));
                    size.appendChild(width);

                    Element height = doc.createElement("height");
                    height.setTextContent(Integer.toString(folderContainer.getOrginalImage().getHeight()));
                    size.appendChild(height);

                    orginalImage.appendChild(size);


                    // Face Image Node
                    Element faceImage = doc.createElement(FACE);
                    // add staff to root

                    // add xml attribute
                    faceImage.setAttribute("id", "1002");

                    // alternative
                    // Attr attr = doc.createAttribute("id");
                    // attr.setValue("1001");
                    // staff.setAttributeNode(attr);

                    name = doc.createElement("name");
                    name.setTextContent(folderContainer.getFaceImage().getName());
                    faceImage.appendChild(name);

                    path = doc.createElement("path");
                    path.setTextContent(folderContainer.getFaceImage().getPath());
                    faceImage.appendChild(path);

                    size = doc.createElement("size");
                    size.setAttribute("w_h", "Width_Height");

                    width = doc.createElement("width");
                    width.setTextContent(Integer.toString(folderContainer.getFaceImage().getWidth()));
                    size.appendChild(width);

                    height = doc.createElement("height");
                    height.setTextContent(Integer.toString(folderContainer.getFaceImage().getHeight()));
                    size.appendChild(height);

                    faceImage.appendChild(size);


                    // Detected Image Node
                    Element detecImage = doc.createElement(DETECTED);
                    // add staff to root

                    // add xml attribute
                    detecImage.setAttribute("id", "1003");

                    // alternative
                    // Attr attr = doc.createAttribute("id");
                    // attr.setValue("1001");
                    // staff.setAttributeNode(attr);

                    name = doc.createElement("name");
                    name.setTextContent(folderContainer.getDetectedImage().getName());
                    detecImage.appendChild(name);

                    path = doc.createElement("path");
                    path.setTextContent(folderContainer.getDetectedImage().getPath());
                    detecImage.appendChild(path);

                    size = doc.createElement("size");
                    size.setAttribute("w_h", "Width_Height");

                    width = doc.createElement("width");
                    width.setTextContent(Integer.toString(folderContainer.getDetectedImage().getWidth()));
                    size.appendChild(width);

                    height = doc.createElement("height");
                    height.setTextContent(Integer.toString(folderContainer.getDetectedImage().getHeight()));
                    size.appendChild(height);

                    detecImage.appendChild(size);


                    // XML  Node
                    Element xmlInfo = doc.createElement(XML);
                    // add staff to root

                    // add xml attribute
                    xmlInfo.setAttribute("id", "1004");

                    // alternative
                    // Attr attr = doc.createAttribute("id");
                    // attr.setValue("1001");
                    // staff.setAttributeNode(attr);

                    name = doc.createElement("name");
                    name.setTextContent(folderContainer.getXmlFileName());
                    xmlInfo.appendChild(name);

                    path = doc.createElement("path");
                    path.setTextContent(folderContainer.getXmlFilePath());
                    xmlInfo.appendChild(path);


                    // Folder Info  Node
                    Element folderInfo = doc.createElement(FOLDER);
                    // add staff to root

                    // add xml attribute
                    folderInfo.setAttribute("id", "1005");

                    name = doc.createElement("name");
                    name.setTextContent(folderContainer.getFolderContainerName());
                    folderInfo.appendChild(name);

                    path = doc.createElement("path");
                    path.setTextContent(folderContainer.getFolderContainerPath());
                    folderInfo.appendChild(path);


                    rootElement.appendChild(orginalImage);
                    rootElement.appendChild(faceImage);
                    rootElement.appendChild(detecImage);
                    rootElement.appendChild(xmlInfo);
                    rootElement.appendChild(folderInfo);


                    doc.appendChild(rootElement);


                    try (FileOutputStream output = new FileOutputStream(folderContainer.getXmlFilePath())) {

                        Utils.writeXml(doc, output);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }


                } catch (IOException | ParserConfigurationException ex) {
                    System.out.println("Can't save picture");
                    ex.printStackTrace(); // Can't save picture

                }
            }

        } else {
            System.out.println("Error Found!");
        }

    }

    @FXML
    private void scanImage() throws IOException {

        String galFilePath = "C:\\Users\\Asus\\IdeaProjects\\OpenBrFaceRecogApp\\galFolder";


        String firstOpenBrCommand = " br -algorithm FaceRecognition -enrollAll -enroll " + scanImageFolderPath + " 'meds.gal' ";
        String firstProcessCommand = "cd " + galFilePath + " ; " + firstOpenBrCommand;

        String secondOpenBrCommand = "  br -algorithm FaceRecognition -compare meds.gal " + scanImagePath + " match_scores.csv ";
        String secondProcessCommand = "cd " + galFilePath + " ; " + secondOpenBrCommand;


        System.out.println(firstProcessCommand);
        System.out.println(secondProcessCommand);


        ProcessBuilder firstBuilder = new ProcessBuilder("powershell.exe", "/c", firstProcessCommand);
        firstBuilder.redirectErrorStream(true);

        try {
            Process p = firstBuilder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }


        ProcessBuilder secondBuilder = new ProcessBuilder("powershell.exe", "/c", secondProcessCommand);
        secondBuilder.redirectErrorStream(true);

        try {
            Process p2 = secondBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }


        String s = null;
        String commandString = null;
        double maxVal = 0;
        String imagePath = "";

        String pythonCommand = "python.exe read_csv.py";
        String command = "cd " + galFilePath + " ; " + pythonCommand;
        // run the Unix "ps -ef" command
        // using the Runtime exec method:

        ProcessBuilder pythoncommand = new ProcessBuilder("powershell.exe", "/c", command);
        pythoncommand.redirectErrorStream(true);

        try {
            Process p3 = pythoncommand.start();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p3.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p3.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                commandString = s;
            }
            String delimiter = ";;;";

            String[] commandArrOfStr = commandString.split(delimiter, 2);

            maxVal = Double.parseDouble(commandArrOfStr[1]);
            imagePath = commandArrOfStr[0];

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (maxVal > 2.0) {

            File f = new File(imagePath);
            Desktop dt = Desktop.getDesktop();
            dt.open(f);

        } else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bilgi");
            alert.setHeaderText(null);
            alert.setContentText("Yüz Tanımlanamadı,  veri tabanına kaydedildi.");
            alert.showAndWait();
            File scanDetectedImage = new File(scanImagePathToSave);
            if (scanDetectedImage.exists()) {
                ImageIO.write(SwingFXUtils.fromFXImage(capturedImage, null), "PNG", scanDetectedImage);
            }


        }
    }


    private class VideoTacker extends Thread {
        @Override
        public void run() {
            while (!isCapture) { // For each 30 millisecond take picture and set it in image view
                try {
                    Image webCamImage = SwingFXUtils.toFXImage(webcam.getImage(), null);
                    imageView.setImage(webCamImage);

                    sleep(0);

                } catch (InterruptedException ex) {
                    Logger.getLogger(CameraCaptureController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }


    private Mat detectFace(Mat frame) {

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();
        Rect rectCrop = new Rect();


        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();


        isFaceInImage = facesArray.length > 0 ? true : false;

        for (int i = 0; i < facesArray.length; i++) {

            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
            rectCrop = facesArray[i];
        }

        faceCvImage = new Mat(frame, rectCrop);
        return frame;

    }


}
