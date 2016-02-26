
package server;

/**
 *
 * @author Eugeny
 */
public class TriBean {
    private int n;
    private int yours;
    private int correct;

    public TriBean(int n, int yours, int correct) {
        this.n = n;
        this.yours = yours;
        this.correct = correct;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getYours() {
        return yours;
    }

    public void setYours(int yours) {
        this.yours = yours;
    }
    
    
}
