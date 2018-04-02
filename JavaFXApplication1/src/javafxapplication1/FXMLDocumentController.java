/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;


public class FXMLDocumentController implements Initializable {
    
    @FXML // fx:id="chooseExamPdfBtn"
    private Button GetPdf; // Value injected by FXMLLoader
    
    @FXML // fx:id="choosePngPathBtn"
    private Button GetPngPath; 
    
    
    
    public String PdfPath = null;
    public String PngPath = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            File file = new File("C:\\Users\\UĞUR\\Desktop\\qrcode2.png");
            String decodedText = decodeQRCode(file);
            if(decodedText == null) {
                System.out.println("No QR Code found in the image");
            } else {
                System.out.println("Decoded text = " + decodedText);
            }
        } catch (IOException e) {
            System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
        }
        
        
        
       
        
        
        
        GetPdf.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

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
        });
        GetPngPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                Stage stage = new Stage();
                File selectedDirectory = directoryChooser.showDialog(stage);
                System.out.println("folder " + selectedDirectory.getAbsolutePath());
                PngPath = selectedDirectory.getAbsolutePath();
                try {
                    PDFToPNG(PdfPath,PngPath);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
            
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
            System.out.println("page " + (page+1) + "finished...");
        }
        document.close();

    }
    private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }
    
}