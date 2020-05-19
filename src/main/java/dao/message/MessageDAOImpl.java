package dao.message;

import dao.DBUtil;
import org.apache.log4j.Logger;
import util.ApplicationException;
import util.AuditLogger;
import vo.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOImpl implements IMessageDAO {

    static final Logger LOGGER = Logger.getLogger("applicationError");

    @Override
    public void insertIntoMessageMaster(Message messageDetails) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement("insert into MessageExchanges(PictureId, MessageFrom, MessageTo, MessageText, MessageTime) values(?, ?, ?, ?, ?) ");
            preparedStatement.setInt(1, messageDetails.getPictureId());
            //pstmt.setInt(2, messageDetails.getComponentId());
            preparedStatement.setString(2, messageDetails.getMessageFrom());
            preparedStatement.setString(3, messageDetails.getMessageTo());
            preparedStatement.setString(4, messageDetails.getMessageText());
            preparedStatement.setString(5, messageDetails.getMessageTime());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            AuditLogger.auditLog(messageDetails.getMessageFrom(),"Send message", "Failed to send messages due to SQL Exception ");
            LOGGER.error("In MessageDAOImpl.insertIntoMessageMaster : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in MessageDAOImpl.insertIntoMessageMaster :"+e.getMessage());
        } finally {
            DBUtil.closeResources(preparedStatement, connection);
        }
    }

    @Override
    public List<Message> getMessagesForClient(int pictureId, String architectName, String clientName)  {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Message> messageList = new ArrayList<>();
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select MessageText, MessageTime, MessageFrom, MessageTo   from MessageExchanges where PictureId=" + pictureId + " and ( MessageFrom='" + architectName + "' or MessageFrom='" + clientName + "' ) " + " and ( MessageTo='" + architectName + "' or MessageTo='" + clientName + "') order by MessageTime ASC");
            while (resultSet.next()) {
                Message message = new Message();
                message.setMessageText(resultSet.getString("MessageText"));
                String messageTime=resultSet.getString("MessageTime");
                message.setMessageTime(messageTime);
                message.setMessageFrom(resultSet.getString("MessageFrom"));
                message.setMessageTo(resultSet.getString("MessageTo"));
                messageList.add(message);
            }
            return messageList;
        } catch (SQLException e) {
            AuditLogger.auditLog("","Receive message", "Failed to receive messages due to SQL Exception ");
            LOGGER.error("In MessageDAOImpl.getMessagesForClient : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in MessageDAOImpl.getMessagesForClient :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet, statement, connection);
        }
    }

}
