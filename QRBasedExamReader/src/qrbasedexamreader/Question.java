package qrbasedexamreader;


public class Question {
    public int questionRank;
    public int maxMark;
    
    Question(){
        
    }
    
    public Question(int questionRank, int maxMark){
        this.questionRank = questionRank;
        this.maxMark = maxMark;
    }
    
    public String toString(){
        return "(" + "Q" + questionRank + ": " + maxMark + ")";
    }

    public int getQuestionRank() {
        return questionRank;
    }

    public int getMaxMark() {
        return maxMark;
    }
}
