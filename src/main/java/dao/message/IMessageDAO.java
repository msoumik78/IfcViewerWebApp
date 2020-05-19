package dao.message;

import vo.Message;

import java.util.List;

public interface IMessageDAO {
    public void insertIntoMessageMaster(Message messageDetails);
    public List<Message> getMessagesForClient(int pictureId, String architectName, String clientName);
}
