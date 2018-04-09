package qrbasedexamreader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class Menu2Controller implements Initializable {

    @FXML
    private Pane choosePane;
    @FXML
    private Pane chooseSubPane;
    @FXML
    private Button choosePDFButton;
    @FXML
    private Button choosePNGButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button exitButton;
    @FXML
    private ImageView mefLogo;
    
    public String PdfPath = null;
    public String PngPath = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void choosePDFPath(MouseEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        chooser.setTitle("Select EXAM");
        Stage stage = new Stage();
        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }           
        PdfPath = file.getAbsolutePath();
    }

    @FXML
    private void choosePNGPath(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);
        System.out.println("folder " + selectedDirectory.getAbsolutePath());
        PngPath = selectedDirectory.getAbsolutePath();
        try {           
            PDFToPNG(PdfPath,PngPath);
        }catch (IOException ex) {
            Logger.getLogger(Menu2Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void goNextWindow(MouseEvent event) throws IOException {
        Stage stage = (Stage) nextButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("menu3.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    
    private void PDFToPNG(String inputPdfPath, String outputPngPath) throws IOException {
        File f = new File(outputPngPath);
        if (!f.exists()) {
            f.mkdir();
        }
        PDDocument document = PDDocument.load(new File(inputPdfPath));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(bim, outputPngPath + "/Page " + (page + 1) + ".png", 300);
        }
        document.close();
    }   
}