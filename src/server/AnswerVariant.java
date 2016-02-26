
package server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eugeny
 */
public class AnswerVariant implements Serializable{
    private List<String> variants;

    public AnswerVariant(String[] answers, int selected) {
        variants = new ArrayList<>(answers.length);
        for (int i=0; i<answers.length; i++) {
            if (selected==i) {
                variants.add(String.format("<input type=\"radio\" name=\"q\" value=\"%d\" checked=\"checked\"/> <span class=\"answer\">%s</span> <br/>", i, answers[i]));
            } else {
                variants.add(String.format("<input type=\"radio\" name=\"q\" value=\"%d\" /> <span class=\"answer\">%s</span> <br/>", i, answers[i]));
            }
        }
    }
    
    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }
    
    
}
