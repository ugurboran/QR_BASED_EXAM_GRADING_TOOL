package qrbasedexamreader;

public class Exam {
    public String courseID;
    public String examDate;
    public String examType;
    public int examPage;
    
    Exam(){
        
    }
    
    public Exam(String courseID, String examDate, String examType, int examPage){
        this.courseID = courseID;
        this.examDate = examDate;
        this.examType = examType;
        this.examPage = examPage;
    }

    public String toString(){
        return courseID + ", " + examDate + ", " + examType + ", " + examPage;
    }
}
