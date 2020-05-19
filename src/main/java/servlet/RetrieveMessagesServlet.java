package servlet;

import dao.message.IMessageDAO;
import vo.Message;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/RetrieveMessages")
@MultipartConfig
public class RetrieveMessagesServlet extends HttpServlet {

	@Inject
	IMessageDAO iMessageDAO;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		List<Message> messageList = null;
		try {
			messageList = iMessageDAO.getMessagesForClient(1,  "testArchitect1", "testClient12");
		} catch (Exception e) {
			System.out.println("Exception encountered in DB call: "+e.getMessage());
		}	
		if (null != messageList) {
			System.out.println("Size of messageList : "+messageList.size());
		}

		request.setAttribute("MessageList", messageList);
		
		String url = "/clientHome.jsp";
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(url);
		dispatcher.forward(request, response);			

	}
}