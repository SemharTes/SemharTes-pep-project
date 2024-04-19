package Service;


import DAO.MessageDAO;
import Model.Message;


import java.util.List;


public class MessageService {
   private final MessageDAO messageDAO;


   public MessageService() {
       messageDAO = new MessageDAO();
   }


   public MessageService(MessageDAO messageDAO) {
       this.messageDAO = messageDAO;
   }


   public Message createMessage(Message message) {
       return messageDAO.createMessage(message);
   }


   public List<Message> getAllMessages() {
       return messageDAO.getAllMessages();
   }


   public Message getMessageById(int message_id) {
       return messageDAO.getMessageById(message_id);
   }


   public Message deleteMessage(int messageId) {
       // Call the DAO method to delete the message
       Message deletedMessage = messageDAO.deleteMessageById(messageId);
      
       if (deletedMessage != null) {
           return deletedMessage;
       } else {
           // Message does not exist, return an empty message object
           return new Message();
       }
   }




   public Message updateMessage(Message message) {
    // Check if the message text is empty or exceeds 255 characters
    if (message.getMessage_text() == null || message.getMessage_text().isEmpty() || message.getMessage_text().length() > 255) {
        return null;
    }
    // Call the DAO method to update the message
       return messageDAO.updateMessage(message);
   }



   public List<Message> getMessagesByUserId(int userId) {
       return messageDAO.getMessagesByUserId(userId);
   }
}
