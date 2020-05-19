package servlet.login;

import dao.user.IUserDAO;
import io.prometheus.client.Counter;
import util.AuditLogger;
import util.MiscUtil;
import util.RedirectionDetails;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/Authenticate")
public class AuthenticationServlet extends HttpServlet {

    // Prometheus counters for successful and failed login
    static final Counter requestsSuccessfulLogin = Counter.build()
            .name("successful_login")
            .help("Number of Successful login").register();

    static final Counter requestsFailedLogin = Counter.build()
            .name("failed_login")
            .help("Number of Failed login").register();

    @Inject
    IUserDAO iUserDAO;


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter(LoginConstants.LoginPageUsername);
        String userRole = authenticateUserAndRetrieveRole(userName, request);
        RedirectionDetails redirectionDetails= decideRedirectDetailsBasedOnRole(request,userRole, userName);
        MiscUtil.redirect(request, response, redirectionDetails.getRedirectPageType(), redirectionDetails.getRedirectPageURL());
       }

       private String authenticateUserAndRetrieveRole(String userName, HttpServletRequest request){
           String password = request.getParameter(LoginConstants.LoginPagePassword);
           return iUserDAO.authenticateUser(userName,password);
       }

       private RedirectionDetails decideRedirectDetailsBasedOnRole(HttpServletRequest request, String userRole, String userName) throws ServletException, IOException {
           RedirectionDetails redirectionDetails = new RedirectionDetails();
           switch (userRole) {
               case "NOT_FOUND":
                   requestsFailedLogin.inc();
                   redirectionDetails.setRedirectPageType("TO_JSP");
                   redirectionDetails.setRedirectPageURL("/login.jsp");
                   request.setAttribute("AuthenticationError","Exists");
                   AuditLogger.auditLog(userName,"Login", "Login is not possible as role not found");
                   break;
               default:
                   setUserInSession(request);
                   requestsSuccessfulLogin.inc();
                   if ("ARCHITECT".equals(userRole)) {
                       redirectionDetails.setRedirectPageType("TO_SERVLET");
                       redirectionDetails.setRedirectPageURL("GetArchitectHome");
                       AuditLogger.auditLog(userName,"Login", "Login successful in Architect role");
                   } else if ("CLIENT".equals(userRole)) {
                       redirectionDetails.setRedirectPageType("TO_SERVLET");
                       redirectionDetails.setRedirectPageURL("GetClientHome");
                       AuditLogger.auditLog(userName,"Login", "Login successful in Client role");
                   }
                   break;
                }
               return redirectionDetails;
        }

        private void setUserInSession(HttpServletRequest request){
            HttpSession session = request.getSession(false);
            session.setAttribute("UserName", request.getParameter(LoginConstants.LoginPageUsername));
        }

}
