package dao.file;

import vo.IfcFile;

import java.util.List;

public interface IFileDAO {
    public void insertIntoFileMaster(int lastId, IfcFile fileDetails);
    public int getMaxIdFromFileMaster();
    public String getFileNameForPictureId(int pictureId);
    public int getLatestPictureIdForClient(String clientName);
    public List<IfcFile> getPicturesForClient(String clientName);
}
