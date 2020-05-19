package servlet.architect;

import dao.file.IFileDAO;
import dao.message.IMessageDAO;
import io.prometheus.client.Counter;
import org.apache.log4j.Logger;
import util.ApplicationException;
import util.AuditLogger;
import util.MiscUtil;
import vo.IfcFile;
import vo.Message;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.out;


@WebServlet("/UploadIfcFile")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    @Inject
    IFileDAO iFileDAO;

    @Inject
    IMessageDAO iMessageDAO;

    static final Logger LOGGER = Logger.getLogger("applicationError");

    // Prometheus counters for file upload and successful conversion
    static final Counter fileUploadCounter = Counter.build()
            .name("file_upload")
            .help("Number of Successful file uploads").register();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Receive the file from jsp
        Part filePart = request.getPart("ifcFileUpload");
        InputStream fileInputStream = filePart.getInputStream();

        //Retrieve other form data
        String customerName = request.getParameter("customerName");
        String messageBody = request.getParameter("messageDetails");

        // Retrieve architect name from the session
        HttpSession session = request.getSession(false);
        String architectName = (String) session.getAttribute("UserName");

        // Archive the original file locally
        String filenameIdentifier = "_" + architectName + "_" + customerName + "_" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String originalFileNameWithAdditionalIdentifier = filePart.getSubmittedFileName().replace(".ifc", "") + filenameIdentifier + ".ifc";
        File fileToSave = new File(MiscUtil.originalFileArchiveFolder + originalFileNameWithAdditionalIdentifier);
        Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);

        String transformedFileNameWithAdditionalIdentifier = convertIntoWexbimFormat(architectName,originalFileNameWithAdditionalIdentifier, fileToSave);

        int iPictureId = insertFileDetailsIntoDB(customerName, architectName, originalFileNameWithAdditionalIdentifier, transformedFileNameWithAdditionalIdentifier);

        insertMessageDetailsInDB(customerName, messageBody, architectName, iPictureId);

        AuditLogger.auditLog(architectName,"File Upload", "File upload successful for client :"+customerName+", file : "+transformedFileNameWithAdditionalIdentifier);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("File Uploaded successfully");
    }


    private String convertIntoWexbimFormat(String architectName, String originalFileNameWithAdditionalIdentifier, File fileToSave) throws IOException {
        // Converting into wexbim format
        String transformedFileNameWithAdditionalIdentifier = originalFileNameWithAdditionalIdentifier.replace(".ifc", ".wexbim");
        String transformedFileFullPath = MiscUtil.transformedFileArchiveFolder + "/" + transformedFileNameWithAdditionalIdentifier;
        String commandTobeExecuted = MiscUtil.transformedCommand + "   " + fileToSave.getPath() + "   " + transformedFileFullPath;
        try {
            Runtime.getRuntime().exec(commandTobeExecuted);
            fileUploadCounter.inc();
        } catch (IOException e) {
            AuditLogger.auditLog(architectName,"Convert to Wexbim format", "Failed to convert to wexbim  : filename - "+originalFileNameWithAdditionalIdentifier);
            LOGGER.error("In FileUploadServlet.convertIntoWexbimFormat : IOException encountered ", e);
            throw new ApplicationException("IOException encountered in FileUploadServlet.convertIntoWexbimFormat :"+e.getMessage());
        }
        return transformedFileNameWithAdditionalIdentifier;
    }

    private void insertMessageDetailsInDB(String customerName, String messageBody, String architectName, int iPictureId) {
        // Insert message details in db
        Message messageDetails = new Message();
        messageDetails.setPictureId(iPictureId);
        messageDetails.setMessageFrom(architectName);
        messageDetails.setMessageTo(customerName);
        messageDetails.setMessageText(messageBody);

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentDateTime = simpleDateFormat.format(new Date());
        messageDetails.setMessageTime(currentDateTime);

        iMessageDAO.insertIntoMessageMaster(messageDetails);
    }

    private int insertFileDetailsIntoDB(String customerName, String architectName, String originalFileNameWithAdditionalIdentifier, String transformedFileNameWithAdditionalIdentifier) {
        // Insert file details in DB
        IfcFile fileDetails = new IfcFile();
        fileDetails.setOriginalFileName(originalFileNameWithAdditionalIdentifier);
        fileDetails.setTransformedFileName(transformedFileNameWithAdditionalIdentifier);
        fileDetails.setUploadedByArchitect(architectName);
        fileDetails.setUploadedForClient(customerName);

        int iPictureId = iFileDAO.getMaxIdFromFileMaster();
        iFileDAO.insertIntoFileMaster(iPictureId, fileDetails);
        return iPictureId;
    }
}