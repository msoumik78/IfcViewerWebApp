package servlet;

import dao.message.IMessageDAO;
import io.prometheus.client.Counter;
import util.AuditLogger;
import vo.Message;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/SendMessage")
public class SendMessageServlet extends HttpServlet {

    @Inject
    IMessageDAO iMessageDAO;

    // Prometheus counters for successful message sent
    static final Counter requestsSuccessfulLogin = Counter.build()
            .name("successful_messages")
            .help("Number of Successful messages").register();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String senderName = request.getParameter("sender");
        String receiverName = request.getParameter("receiver");
        String messageText = request.getParameter("messageText");
        String pictureId = request.getParameter("pictureId");

        insertMessageDetailsInDB(senderName, receiverName, messageText, pictureId);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Message saved successfully");

    }

    private void insertMessageDetailsInDB(String senderName, String receiverName, String messageText, String pictureId) {
        Message messageDetails = new Message();
        messageDetails.setPictureId(Integer.parseInt(pictureId));
        messageDetails.setMessageFrom(senderName);
        messageDetails.setMessageTo(receiverName);
        messageDetails.setMessageText(messageText);

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentDateTime = simpleDateFormat.format(new Date());
        messageDetails.setMessageTime(currentDateTime);

        iMessageDAO.insertIntoMessageMaster(messageDetails);
        requestsSuccessfulLogin.inc();
        AuditLogger.auditLog(senderName,"Send message", "Send message success to : "+receiverName+" - messageText : "+messageText);

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }


}
