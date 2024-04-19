package DAO;


import Model.Message;
import Util.ConnectionUtil;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MessageDAO {


   public Message createMessage(Message message) {
       String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
           statement.setInt(1, message.getPosted_by());
           statement.setString(2, message.getMessage_text());
           statement.setLong(3, message.getTime_posted_epoch());
           int affectedRows = statement.executeUpdate();
           if (affectedRows == 0) {
               throw new SQLException("Creating message failed, no rows affected.");
           }
           try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
               if (generatedKeys.next()) {
                   message.setMessage_id(generatedKeys.getInt(1));
               } else {
                   throw new SQLException("Creating message failed, no ID obtained.");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return message;
   }


   public List<Message> getAllMessages() {
       List<Message> messages = new ArrayList<>();
       String sql = "SELECT * FROM message";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
           while (resultSet.next()) {
               messages.add(new Message(resultSet.getInt("message_id"),
                       resultSet.getInt("posted_by"),
                       resultSet.getString("message_text"),
                       resultSet.getLong("time_posted_epoch")));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return messages;
   }


   public Message getMessageById(int messageId) {
       String sql = "SELECT * FROM message WHERE message_id = ?";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)) {
           statement.setInt(1, messageId);
           try (ResultSet resultSet = statement.executeQuery()) {
               if (resultSet.next()) {
                   return new Message(resultSet.getInt("message_id"),
                           resultSet.getInt("posted_by"),
                           resultSet.getString("message_text"),
                           resultSet.getLong("time_posted_epoch"));
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return null;
   }


   public Message deleteMessageById(int messageId) {
       Connection conn = ConnectionUtil.getConnection();
       
       try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement prepstm = conn.prepareStatement(sql); 
            prepstm.setInt(1, messageId);
            prepstm.executeQuery();
       
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
       Message message = getMessageById(messageId);
       return message;
   }


   public Message updateMessage(Message message) {
    Connection conn = ConnectionUtil.getConnection();
    int id = message.getMessage_id();
    String text = message.getMessage_text();
    Message updatedMessage = null;

    try {
        if (getMessageById(id) != null) {
            // Check if the message_text is not blank and does not exceed 255 characters
            if (text != null && !text.trim().isEmpty() && text.length() <= 255) {
                String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, text);
                statement.setInt(2, id);
                
                // Execute the update query
                int rowsAffected = statement.executeUpdate();
                
                // Check if the update was successful
                if (rowsAffected > 0) {
                    // If successful, retrieve the updated message
                    updatedMessage = getMessageById(id);
                }
            } else {
                // If the message_text is blank or exceeds 255 characters, throw an exception
                throw new IllegalArgumentException("");
            }
        } else {
            // If the message does not exist, throw an exception
            throw new IllegalArgumentException("");
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
    
    return updatedMessage;
}



   public List<Message> getMessagesByUserId(int userId) {
       List<Message> messages = new ArrayList<>();
       String sql = "SELECT * FROM message WHERE posted_by = ?";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)) {
           statement.setInt(1, userId);
           try (ResultSet resultSet = statement.executeQuery()) {
               while (resultSet.next()) {
                   messages.add(new Message(resultSet.getInt("message_id"),
                           resultSet.getInt("posted_by"),
                           resultSet.getString("message_text"),
                           resultSet.getLong("time_posted_epoch")));
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return messages;
   }
}
