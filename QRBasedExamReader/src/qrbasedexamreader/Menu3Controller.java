package qrbasedexamreader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Menu3Controller implements Initializable {

    @FXML
    private Pane Pane1;
    @FXML
    private Pane Pane2;
    @FXML
    private ImageView mefLogo;
    @FXML
    private Button openPageButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button exitButton;
    
    public String examPngPath = null;
    public String QRCodePath = null;
    public String decodedText = null;
    @FXML
    private ImageView examPageImage = new ImageView();
    
    HashMap<Integer, String> studentInfoMap = new HashMap();
    HashMap<Integer, Exam> studentExamMap = new HashMap();
    HashMap<Integer, ArrayList<Question>> studentQuestionMap = new HashMap();
    HashMap<Integer, Integer> studentTotalGradeMap = new HashMap();
    @FXML
    private TextField markTextField;
    @FXML
    private TextArea commentTextField;
    
    ArrayList<Question> questionList = new ArrayList<>();
    
    public String markText;
    public String commentText;
    
    public int pageCount;
    public int studentID;
    public String courseID;
    public String examDate;
    public String examType;
    
    public int sum;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void openPageImage(MouseEvent event) {
        sum = 0;
        questionList = new ArrayList<>();
        pageCount = 1;
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        chooser.setTitle("Select EXAM PAGE");
        Stage stage = new Stage();
        File file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }           
        examPngPath = file.getAbsolutePath();
        Image image = new Image(file.toURI().toString());
        cropQRCode(examPngPath, ((int)image.getWidth()*76/100), 0, ((int)image.getWidth() - (int)image.getWidth()*76/100), ((int)image.getHeight()*16/100));
        try{
            decodedText = decodeQRCode(new File(QRCodePath + "/QRCodeImage.png"));
            //System.out.println(decodedText);
            ArrayList<Integer> percentages = new ArrayList<>();
            ArrayList<Integer> questionRankforPercentages = new ArrayList<>();
            String[] order = decodedText.split(",");
            for(int i = 0; i < order.length; i++){
                System.out.println(order[i]);
            }
            courseID = order[0];
            examDate = order[1];
            examType = order[2];
            int examPage = Integer.parseInt(order[3]);
            Exam exam1 = new Exam(courseID, examDate, examType, examPage);
            String studentName = order[4];
            studentID = Integer.parseInt(order[5]);
            studentInfoMap.put(studentID, studentName);
            studentExamMap.put(studentID, exam1);
            //System.out.println(order[6]);
            for(int j = 6; j < order.length; j++){
                String[] newOrder = order[j].split("[ \\\\{,.%()\\\\}]");
                int questionRank = Integer.parseInt(newOrder[1]);
                int maxMark = Integer.parseInt(newOrder[2]);
                int percentage = Integer.parseInt(newOrder[4]);
                percentages.add(percentage);
                questionRankforPercentages.add(questionRank);
            }
            cropImage(examPngPath, percentages, questionRankforPercentages, image);
        } catch(IOException e){
            System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
        }
        //ImageView iv = new ImageView(image);
        examPageImage.setImage(image);
    }

    @FXML
    private void goNextWindow(MouseEvent event) throws IOException {
        File file = new File(QRCodePath + "/Question" + pageCount + ".png");
        Image image = new Image(file.toURI().toString());
        examPageImage.setImage(image);
        pageCount++;
    }

    @FXML
    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    
    public static BufferedImage readImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
            return img;
        } catch (IOException e) {
            System.err.println("Image cannot be read");
        }
        return null;
    }
    
    public void cropImage(String filePath, ArrayList<Integer> percentages, ArrayList<Integer> questionRankforPercentages, Image image) {

        ArrayList<BufferedImage> partsOfImage = new ArrayList<>();
        BufferedImage originalImage = readImage(filePath);
        BufferedImage subImage = null;
        try {
            for(int i = 0; i < percentages.size(); i++){
                if (i + 1 != percentages.size()) {
                    subImage = originalImage.getSubimage(0, (int)image.getHeight()*percentages.get(i)/100, (int)image.getWidth(), (int)image.getHeight()*percentages.get(i+1)/100 - (int)image.getHeight()*percentages.get(i)/100);
                }
                else {
                    subImage = originalImage.getSubimage(0, (int)image.getHeight()*percentages.get(i)/100, (int)image.getWidth(), (int)image.getHeight() - (int)image.getHeight()*percentages.get(i)/100);
                }
                ImageIO.write(subImage, "png", new File(QRCodePath + "/Question" + questionRankforPercentages.get(i) +".png"));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void cropQRCode(String filePath, int x, int y, int w, int h) {

        ArrayList<BufferedImage> partsOfImage = new ArrayList<>();
        BufferedImage originalImage = readImage(filePath);
        BufferedImage subImage = null;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);
        QRCodePath = selectedDirectory.getAbsolutePath();
        try{      
            subImage = originalImage.getSubimage(x, y, w, h);
            ImageIO.write(subImage, "png", new File(QRCodePath + "/QRCodeImage.png"));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static String decodeQRCode(File qrCodeimage) throws IOException {
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

    @FXML
    private void finishExcel(MouseEvent event) throws FileNotFoundException, IOException {
        Set<Map.Entry<Integer, String>> entrySetInfo = studentInfoMap.entrySet();
        Set<Map.Entry<Integer, Exam>> entrySetExam = studentExamMap.entrySet();
        Set<Map.Entry<Integer, ArrayList<Question>>> entrySetQuestion = studentQuestionMap.entrySet();
        Set<Map.Entry<Integer, Integer>> entrySetTotalGrade = studentTotalGradeMap.entrySet();
        
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Öğrenci notları");
        
        int numberOfStudent = studentInfoMap.size();
        Object[][] bookData = new Object[numberOfStudent + 2][4];
        int i = 2;
        bookData[0][0] = courseID;
        bookData[0][1] = examDate;
        bookData[0][2] = examType;
        
        bookData[1][0] = ("Öğrenci Numaraları");
        bookData[1][1] = ("Öğrenci İsimleri");
        bookData[1][2] = ("Sorulardan alınan notlar");
        bookData[1][3] = ("Toplam Not");
        
        for(Map.Entry<Integer, String> entryInfo : entrySetInfo) {
            for(Map.Entry<Integer, Exam> entryExam : entrySetExam){
                for(Map.Entry<Integer, ArrayList<Question>> entryQuestion : entrySetQuestion){
                    for(Map.Entry<Integer, Integer> entryTotalGrade : entrySetTotalGrade){
                        if(entryInfo.getKey().equals(entryExam.getKey()) && entryInfo.getKey().equals(entryQuestion.getKey()) && entryInfo.getKey().equals(entryTotalGrade.getKey())){
                            System.out.println("Student ID: " + entryInfo.getKey() + ", Student Name: " + entryInfo.getValue() + ", Questions: " + entryQuestion.getValue() + ", Total: " + entryTotalGrade.getValue());
                            bookData[i][0] = entryInfo.getKey();
                            bookData[i][1] = entryInfo.getValue();
                            bookData[i][2] = entryQuestion.getValue().toString();
                            bookData[i][3] = entryTotalGrade.getValue();
                            i++;
                            
                        }
                    }
                }
            }
        }
        
        int rowCount = 0;
        for (Object[] aBook : bookData) {
            Row row = sheet.createRow(++rowCount);           
            int columnCount = 0;       
            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double){
                    cell.setCellValue((Double) field);
                }
            }
             
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = new Stage();
        File selectedDirectory = directoryChooser.showDialog(stage);
        String excelPath = selectedDirectory.getAbsolutePath();
        try (FileOutputStream outputStream = new FileOutputStream(excelPath + "/" + courseID + "-" + examType + ".xlsx")) {
            workbook.write(outputStream);
        }
    }

    @FXML
    private void submitTextFields(MouseEvent event) {
        markText= markTextField.getText();
        sum += Integer.parseInt(markText);
        commentText = commentTextField.getText();
        Question question1 = new Question(pageCount - 1, Integer.parseInt(markText));
        questionList.add(question1);
        studentQuestionMap.put(studentID, questionList);
        studentTotalGradeMap.put(studentID, sum);
    }
}