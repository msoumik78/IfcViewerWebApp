package servlet.architect;

import dao.file.IFileDAO;
import dao.message.IMessageDAO;
import dao.user.IUserDAO;
import util.AuditLogger;
import util.MiscUtil;
import vo.IfcFile;
import vo.Message;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/GetArchitectHome")
public class GetArchitectHomeServlet extends HttpServlet {

	@Inject
	IFileDAO iFileDAO;

	@Inject
	IUserDAO iUserDAO;

	@Inject
	IMessageDAO iMessageDAO;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		HttpSession session = request.getSession(false);

		// Required to ensure that the same page loads BUT with correct customer and picture data everytime
		request.removeAttribute("CustomerSelected");
		request.removeAttribute("PictureIdSelected");
		request.removeAttribute("MessageList");

		String architectName=(String)session.getAttribute("UserName");
		String pageLoadReason= request.getParameter("pageLoadReason");

		// Only the client list is loaded when the page loads for first time
		retrieveClientList(session, architectName);

		// Get the chosen client from the frontend
		String chosenClient = request.getParameter("chosenClient");

		loadPageDataWhenNotComingFromLoginPage(request, architectName, pageLoadReason, chosenClient);

		AuditLogger.auditLog(architectName,"Get Architect HomePage", "About to display home page - pageLoadReason :"+pageLoadReason+" - chosenClient :"+chosenClient);

		MiscUtil.redirect(request, response, "TO_JSP","/architect_home1.jsp");

	}

	private void retrieveClientList(HttpSession session, String architectName) {
		//Get List of clients for the architect
		List<String> clientList = (List<String>) session.getAttribute("ClientList");
		if (clientList == null) {
				clientList = iUserDAO.getCustomersForArchitect(architectName);
				if (clientList.size() > 0 ) {
					session.setAttribute("ClientList",clientList);
				}
		}
	}

	private void loadPageDataWhenNotComingFromLoginPage(HttpServletRequest request, String architectName, String pageLoadReason, String chosenClient) {
		List<IfcFile> ifcFileList;
		if (null != pageLoadReason) {
			int pictureId=-1;
			String transformedFileName= null;
			request.setAttribute("CustomerSelected",chosenClient);
			ifcFileList = iFileDAO.getPicturesForClient(chosenClient);
			if (ifcFileList.size() > 0) {
					request.setAttribute("IfcFileList",ifcFileList);
				}
				// Get the chosen picture from the frontend
				String pictureIdStr = request.getParameter("chosenPicture");
				if ("changeCustomer".equals(pageLoadReason)) {
					pictureId = iFileDAO.getLatestPictureIdForClient(chosenClient);
				} else if ("changePicture".equals(pageLoadReason)) {
					pictureId = Integer.parseInt(pictureIdStr);
				}

				loadPictureAndMessagesForSelectedPicture(request, architectName, chosenClient, pictureId, transformedFileName);
		}
	}

	private void loadPictureAndMessagesForSelectedPicture(HttpServletRequest request, String architectName, String chosenClient, int pictureId, String transformedFileName) {
		request.setAttribute("PictureIdSelected",String.valueOf(pictureId));
		transformedFileName= iFileDAO.getFileNameForPictureId(pictureId);

		//transformedFileName = "/wexbimFiles"+"/"+"SampleHouse.wexbim";
		transformedFileName = "/wexbimFiles"+"/"+transformedFileName;

		request.setAttribute("TransformedFileLocation",transformedFileName);
		request.setAttribute("Collapse2","true");
		retrieveMessages(request, architectName, chosenClient, pictureId);

	}

	private void retrieveMessages(HttpServletRequest request, String architectName, String chosenClient, int pictureId) {
		// Retrieve messages corresponding to the specific pictureId
		List<Message> messageList = null;
		if ((null != chosenClient) && (pictureId != -1)) {
				messageList = iMessageDAO.getMessagesForClient(pictureId,  architectName, chosenClient);
				if (messageList.size() > 0) {
					request.setAttribute("MessageList", messageList);
				}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}
}