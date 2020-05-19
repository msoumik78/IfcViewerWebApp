package vo;

import lombok.*;
import java.util.Date;

/**
 * @author Soumik
 *
 */
@Getter
@Setter
public class IfcFile {

private int pictureId;
private String originalFileName;
private String transformedFileName;
private String uploadedByArchitect;
private String uploadedForClient;
//private Date uploadTime;

	
}