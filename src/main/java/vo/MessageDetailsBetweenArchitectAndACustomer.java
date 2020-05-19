package vo;

import lombok.*;
import java.util.List;

/**
 * @author Soumik
 *
 */
@Getter
@Setter
public class MessageDetailsBetweenArchitectAndACustomer {

private String customerName;
private int unreadMessageCount;
private List<Message> messageList;

	
}