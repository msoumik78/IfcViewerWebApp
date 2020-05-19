package dao.file;

import dao.DBUtil;
import org.apache.log4j.Logger;
import util.ApplicationException;
import util.AuditLogger;
import vo.IfcFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAOImpl implements IFileDAO {

    static final Logger LOGGER = Logger.getLogger("applicationError");

    @Override
    public void insertIntoFileMaster(int lastId, IfcFile fileDetails)  {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement("insert into FileMaster(PictureId, OriginalFileName, TransformedFileName, UploadedByArchitect, UploadedForClient) values(?, ?, ?, ?, ?) ");
            preparedStatement.setInt(1, lastId + 1);
            preparedStatement.setString(2, fileDetails.getOriginalFileName());
            preparedStatement.setString(3, fileDetails.getTransformedFileName());
            preparedStatement.setString(4, fileDetails.getUploadedByArchitect());
            preparedStatement.setString(5, fileDetails.getUploadedForClient());
            //pstmt.setDate(6, fileDetails.getUploadTime());
        } catch (SQLException e) {
            AuditLogger.auditLog("","Insert File Name", "Failed to insert file name due to SQL Exception - file name : "+fileDetails.getOriginalFileName());
            LOGGER.error("In FileDAOImpl.insertIntoFileMaster : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in FileDAOImpl.insertIntoFileMaster :"+e.getMessage());
        } finally {
            DBUtil.closeResources(preparedStatement,connection);
        }
    }

    @Override
    public int getMaxIdFromFileMaster()  {
        int maxPictureId = -1;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select max(PictureId) as maxId from FileMaster ");
            while (resultSet.next()) {
                maxPictureId = resultSet.getInt("maxId");
            }
            return maxPictureId;
        } catch (SQLException e) {
            AuditLogger.auditLog("","Get max file Id", "Failed to get max file id due to SQL Exception ");
            LOGGER.error("In FileDAOImpl.getMaxIdFromFileMaster : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in FileDAOImpl.getMaxIdFromFileMaster :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet,statement,connection);
        }
    }


    @Override
    public String getFileNameForPictureId(int pictureId) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String transformedFileName=null;
        try {
            connection = DBUtil.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select TransformedFileName from FileMaster where PictureId="+pictureId);
            while (rs.next()) {
                transformedFileName = rs.getString(1);
            }
            return transformedFileName;
        } catch (SQLException e) {
            AuditLogger.auditLog("","Get file name for file id", "Failed to get file name due to SQL Exception - file id  :"+pictureId);
            LOGGER.error("In FileDAOImpl.getFileNameForPictureId : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in FileDAOImpl.getFileNameForPictureId :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public int  getLatestPictureIdForClient(String clientName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int pictureId=0;
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select max(PictureId) from FileMaster where UploadedForClient='"+clientName+"'");
            while (resultSet.next()) {
                pictureId= resultSet.getInt(1);
            }
            return pictureId;
        } catch (SQLException e) {
            AuditLogger.auditLog("","Get latest file id", "Failed to get latest picture id due to SQL Exception - clienName :"+clientName);
            LOGGER.error("In FileDAOImpl.getLatestPictureIdForClient : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in FileDAOImpl.getLatestPictureIdForClient :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet, statement, connection);
        }
    }

    @Override
    public List<IfcFile> getPicturesForClient(String clientName)  {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<IfcFile> ifcFileList = new ArrayList<>();
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select PictureId, OriginalFileName, TransformedFileName from FileMaster where UploadedForClient='"+clientName+"'");
            while (resultSet.next()) {
                int pictureId = resultSet.getInt(1);
                String originalFileName = resultSet.getString(2);
                String transformedFileName = resultSet.getString(3);
                IfcFile ifcFile = new IfcFile();
                ifcFile.setPictureId(pictureId);
                ifcFile.setOriginalFileName(originalFileName);
                ifcFile.setTransformedFileName(transformedFileName);
                ifcFileList.add(ifcFile);
            }
            return ifcFileList;
        } catch (SQLException e) {
            AuditLogger.auditLog("","Get pictures for client", "Failed to get pictures due to SQL Exception - clienName :"+clientName);
            LOGGER.error("In FileDAOImpl.getPicturesForClient : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in FileDAOImpl.getPicturesForClient :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet, statement, connection);
        }
    }



}
