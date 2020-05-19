package servlet.customer;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import java.util.*;

import dao.*;
import dao.file.IFileDAO;
import dao.message.IMessageDAO;
import dao.user.IUserDAO;
import util.AuditLogger;
import util.MiscUtil;
import vo.*;

@WebServlet("/GetClientHome")
public class GetClientHomeServlet extends HttpServlet {

    @Inject
    IFileDAO iFileDAO;

    @Inject
    IUserDAO iUserDAO;

    @Inject
    IMessageDAO iMessageDAO;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        int pictureId=-1;

        String clientName=(String)session.getAttribute("UserName");
        String architectName = determineArchitectName(clientName, session);

        retrieveFileListForClient(clientName, session);
        pictureId = determinePictureId(clientName, request);
        retrieveMessages(pictureId, architectName, clientName, request);
        String transformedFileName = "/wexbimFiles"+"/"+iFileDAO.getFileNameForPictureId(pictureId);

        if (pictureId != -1) {
            request.setAttribute("PictureIdSelected",String.valueOf(pictureId));
            request.setAttribute("TransformedFileLocation",transformedFileName);
        }
        AuditLogger.auditLog(clientName,"Get Client HomePage", "About to display home page for picture : "+transformedFileName);
        MiscUtil.redirect(request, response, "TO_JSP","/clientHome.jsp");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    private String determineArchitectName(String clientName, HttpSession session){
        String architectName = (String)session.getAttribute("ArchitectNameForClient");
        if (null == architectName) {
                architectName = iUserDAO.getArchitectForClient(clientName);
                session.setAttribute("ArchitectNameForClient",architectName);
        }
        return architectName;
    }

    private void retrieveFileListForClient(String clientName, HttpSession session){
        if (null == session.getAttribute("IfcFileList")) {
                List<IfcFile> ifcFileList = iFileDAO.getPicturesForClient(clientName);
                session.setAttribute("IfcFileList",ifcFileList);
        }
    }

    private int determinePictureId(String clientName, HttpServletRequest request){
        String pictureIdStr = request.getParameter("pictureId");
        if (null == pictureIdStr) {
                return iFileDAO.getLatestPictureIdForClient(clientName);
        } else {
            return Integer.parseInt(pictureIdStr);
        }
    }

    private void retrieveMessages(int pictureId, String architectName, String clientName, HttpServletRequest request){
        // Retrieve messages corresponding to the specific pictureId
        List<Message> messageList = null;
           messageList = iMessageDAO.getMessagesForClient(pictureId,  architectName, clientName);
            if (messageList.size() > 0) {
                request.setAttribute("MessageList", messageList);
            }
        }

}