
package server;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Eugeny
 */
public class Question implements Serializable {
    private String question;
    private String picture;
    private String addition;
    private String[] answers;
    private int correct;

    public Question(String question, String picture, String addition, String[] answers, int correct) {
        this.question = question;
        this.picture = picture;
        this.addition = addition;
        this.answers = answers;
        this.correct = correct;
    }


    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }

    @Override
    public String toString() {
        return "Question{" + "question=" + question + ", picture=" + picture + ", addition=" + addition + ", answers=" + Arrays.toString(answers) + ", correct=" + correct + '}';
    }
    
    
}
