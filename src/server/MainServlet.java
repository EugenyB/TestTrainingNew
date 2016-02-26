package server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by eugeny on 26.02.2016.
 */
@WebServlet(name = "MainServlet", urlPatterns = {"/starttest","/run","/sendanswer","/finish"})
public class MainServlet extends HttpServlet {

    public static final String SITE_ROOT = "http://www.berkut.mk.ua";
    public static final int TOTAL_QUESTIONS = 50;
    List<Question> block1;
    List<Question> block2;
    List<Question> block3;
    List<Question> block4;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        switch (request.getServletPath()){
            case "/starttest": doStart(request,response);
                break;
            case "/run": doRun(request,response);
                break;
            case "/sendanswer": doAnswer(request,response);
                break;
            case "/finish": doFinish(request,response);
                break;
        }
    }

    private void doFinish(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        int[] answers = (int[]) session.getAttribute("answers");
        List<TriBean> result = new ArrayList<>();
        int totalCorrect = 0;
        for (int i=0; i<answers.length; i++) {
            TriBean tb = new TriBean(i, answers[i], questions.get(i).getCorrect());
            if (answers[i] == questions.get(i).getCorrect()) {
                totalCorrect++;
            }
            result.add(tb);
        }
        request.setAttribute("result", result);
        request.setAttribute("correct", totalCorrect);
        request.setAttribute("total", TOTAL_QUESTIONS);
        request.getRequestDispatcher("/finish.jsp").forward(request, response);
    }

    private void doAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer current = (Integer)session.getAttribute("current");

        String ans = request.getParameter("ans");
        if ("Next".equals(ans)) {
            current = (current + 1) % TOTAL_QUESTIONS;
        }
        if ("Prev".equals(ans)) {
            current = (current + TOTAL_QUESTIONS-1) % TOTAL_QUESTIONS;
        }
        if ("Accept".equals(ans)) {
            int[] answers = (int[]) session.getAttribute("answers");
            String q = request.getParameter("q");
            if (q!=null) {
                int a = Integer.parseInt(q);
                answers[current] = a;
                session.setAttribute("answers", answers);
            }
            current = (current + 1) % TOTAL_QUESTIONS;
        }
        if ("Finish".equals(ans) && "1".equals(request.getParameter("finish"))) {
            response.sendRedirect("finish");
        } else {
            session.setAttribute("current", current);
            response.sendRedirect("run");
        }
    }

    private void doRun(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("index.html");
        } else {
            Integer current = (Integer)session.getAttribute("current");
            Integer answer = ((int[])session.getAttribute("answers"))[current];
            List<Question> questions = (List<Question>) session.getAttribute("questions");
            Question question = questions.get(current);
            String pict = question.getPicture();
            String picture = "";
            if (!pict.trim().isEmpty()){
                picture = "<img src=\"" + SITE_ROOT + "/download/test/img/"+pict+"\" /> <br/>";
            }
            request.setAttribute("picture", picture);
            AnswerVariant av = new AnswerVariant(question.getAnswers(), answer);
            request.setAttribute("av", av);
            request.setAttribute("q", question);
            request.getRequestDispatcher("/run.jsp").forward(request, response);
        }
    }

    private void doStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fio = request.getParameter("fio");
        String group = request.getParameter("group");
        HttpSession session = request.getSession();
        session.setAttribute("group", group);
        session.setAttribute("fio", fio);
        session.setAttribute("starttime", new Date());
        session.setAttribute("current", 0);
        session.setAttribute("total", TOTAL_QUESTIONS);
        List<Question> questions = createTest();
        session.setAttribute("questions", questions);
        int[] answers = new int[TOTAL_QUESTIONS];
        for (int i=0; i<answers.length; i++) {
            answers[i] = -1;
        }
        session.setAttribute("answers", answers);
        response.sendRedirect("run");
    }

    @Override
    public void init() throws ServletException {
        super.init();
        // TODO: Если все тесты из одного блока - он здесь должен загружаться
        block1 = readBlock("q1.txt");
        block2 = readBlock("q2.txt");
        block3 = readBlock("q3.txt");
        block4 = readBlock("q4.txt");
    }

    private List<Question> createTest() {
        //TODO: Если один блок - здесь его обрабатываем, перемешиваем и копируем в result
        List<Question> b1 = new ArrayList<>(block1);
        List<Question> b2 = new ArrayList<>(block2);
        List<Question> b3 = new ArrayList<>(block3);
        List<Question> b4 = new ArrayList<>(block4);
        Collections.shuffle(b1);
        Collections.shuffle(b2);
        Collections.shuffle(b3);
        Collections.shuffle(b4);
        List<Question> result = new ArrayList<>(TOTAL_QUESTIONS);
        result.addAll(b1.subList(0, 10));
        result.addAll(b2.subList(0, 10));
        result.addAll(b3.subList(0, 15));
        result.addAll(b4.subList(0, 15));

        for (Question q : result) {
            System.out.println(q);
        }
        return result;
    }

    private List<Question> readBlock(String fileName) {
        List<Question> block = new ArrayList<>();
        try (InputStream openStream = new URL(SITE_ROOT + "/download/test/" +fileName).openStream()) { // url

            BufferedReader in = new BufferedReader(new InputStreamReader(openStream, "cp1251")); // url
            String question;
            while ((question=in.readLine())!=null) {
                String pictureName = in.readLine();
                String addition = in.readLine();
                String[] answers = new String[4];
                int correct = -1;
                for (int i=0;i<answers.length; i++) {
                    String ans = in.readLine();
                    if (!ans.isEmpty() && ans.charAt(0)=='+') {
                        correct = i;
                        answers[i] = ans.substring(1);
                    } else answers[i] = ans;
                }
                Question q = new Question(question, pictureName, addition, answers, correct);
                block.add(q);
                in.readLine(); // пустая строка
            }
            return block;
        } catch (IOException ex) {
            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return block;
    }
}
