package util;

import dao.DBUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class MiscUtil {

    public static String originalFileArchiveFolder = null;
    public static String transformedFileArchiveFolder = null;
    public static String transformedCommand = null;

    static {
        try (InputStream input = MiscUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
            }
            prop.load(input);
            originalFileArchiveFolder = prop.getProperty("original.file.archive.location");
            transformedFileArchiveFolder = prop.getProperty("transformed.file.archive.location");
            transformedCommand = prop.getProperty("transform.command");

        } catch (Exception ex) {
            System.out.println("!!!!!!!!!!!Exception encountered while loading application.propeerties !!!!!!!!!!!!!!!  :" + ex.getCause());
        }
    }


    public static void redirect(HttpServletRequest request, HttpServletResponse response, String redirectType, String redirectPage) throws ServletException, IOException {
        switch (redirectType) {
            case "TO_JSP":
                RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(redirectPage);
                dispatcher.forward(request, response);
                break;
            case "TO_SERVLET":
                response.sendRedirect(redirectPage);
                break;
        }
    }


}
