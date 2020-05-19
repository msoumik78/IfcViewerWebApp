package servlet.architect;

import dao.user.IUserDAO;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/CustomerCRUD")
public class CustomerCRUDServlet extends HttpServlet {

    @Inject
    IUserDAO iUserDAO;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
     String customerActionType = request.getParameter("customerActionType");
     String customerNameToBeChecked = request.getParameter("customerNameToBeChecked");
     System.out.println("In CustomerCRUD servlet.... received from fe : "+customerNameToBeChecked);
        try {
            System.out.println("In CustomerCRUD servlet....Customer exists : "+ iUserDAO.checkIfUserNameExists(customerNameToBeChecked));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("In CustomerCRUD servlet, exception encountered : "+e.getMessage());
        }
    }
}
