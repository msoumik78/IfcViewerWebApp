package vo;

import lombok.*;

import java.util.Date;

/**
 * @author Soumik
 *
 */
@Getter
@Setter
public class Message {

private int pictureId;
private int componentId;
private String messageFrom;
private String messageTo;
private String messageText;
private String messageTime;

	
}