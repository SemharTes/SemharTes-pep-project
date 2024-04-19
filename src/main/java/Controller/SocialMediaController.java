package Controller;


import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import io.javalin.http.Context;


import java.util.List;


public class SocialMediaController {
   AccountService accountService;
   MessageService messageService;
   ObjectMapper objectMapper;


   public SocialMediaController() {
       this.accountService = new AccountService();
       this.messageService = new MessageService();
       this.objectMapper = new ObjectMapper();
   }


   public Javalin startAPI() {
       Javalin app = Javalin.create();


       // Endpoints for Account operations
       app.post("/register", this::registerAccountHandler);
       app.post("/login", this::loginHandler);


       // Endpoints for Message operations
       app.post("/messages", this::createMessageHandler);
       app.get("/messages", this::getAllMessagesHandler);
       app.get("/messages/{message_id}", this::getMessageByIdHandler); 
       app.delete("/messages/{message_id}", this::deleteMessageHandler); 
       app.patch("/messages/{message_id}", this::updateMessageHandler); 
       app.get("/accounts/{account_id}/messages", this::getMessagesByUserIdHandler);


       return app;
   }


   private void registerAccountHandler(Context ctx) {
       try {
           Account account = objectMapper.readValue(ctx.body(), Account.class);
           Account registeredAccount = accountService.registerAccount(account);
           ctx.json(registeredAccount);
           ctx.status(200);
       } catch (Exception e) {
           ctx.status(400);
           ctx.json(e.getMessage());
       }
   }


   
   private void loginHandler(Context ctx) {
       try {
           String username = ctx.formParam("username");
           String password = ctx.formParam("password");
           Account loggedInAccount = accountService.login(username, password);
           if (loggedInAccount != null) {
               ctx.json(loggedInAccount);
               ctx.status(200);
             
           } else {
               ctx.json("");
               ctx.status(401);
              
           }
       } catch (Exception e) {
           ctx.status(400);
           ctx.json(e.getMessage());
       }
   }




   private void createMessageHandler(Context ctx) {
       try {
           // Read the JSON message from the request body
           Message message = objectMapper.readValue(ctx.body(), Message.class);
          
           // Validate message text length
           String messageText = message.getMessage_text();
           if (messageText == null || messageText.trim().isEmpty() || messageText.length() > 255) {
               ctx.status(400);
               ctx.result("");
               return;
           }


           // Check if the posted_by field refers to an existing user
           int postedBy = message.getPosted_by();
           if (!accountService.doesUserExist(postedBy)) {
               ctx.status(400);
               ctx.json("");
               return;
       }
          
           // Create the message using the MessageService
           Message createdMessage = messageService.createMessage(message);
          
           // Return the created message in the response body
           ctx.json(createdMessage);
           ctx.status(200);
       } catch (Exception e) {
           // If an exception occurs, return a 400 Bad Request response with the error message
           ctx.status(400);
           ctx.result("Error: " + e.getMessage());
       }
   }   
  
  


   private void getAllMessagesHandler(Context ctx) {
       try {
           List<Message> messages = messageService.getAllMessages();
           ctx.json(messages);
           ctx.status(200);
       } catch (Exception e) {
           ctx.status(500);
           ctx.json("Error: " + e.getMessage());
       }
   }
  
   private void getMessageByIdHandler(Context ctx) {
       try {
           int messageId = Integer.parseInt(ctx.pathParam("message_id"));
           Message message = messageService.getMessageById(messageId);
           if (message != null) {
               ctx.status(200).json(message);
           } else {
               ctx.status(200).result("");
           }
       } catch (NumberFormatException e) {
           ctx.status(400);
           ctx.json("Invalid message ID");
       } catch (Exception e) {
           ctx.status(500);
           ctx.json("Error: " + e.getMessage());
       }
   }
  
   private void deleteMessageHandler(Context ctx) {
       try {
           int messageId = Integer.parseInt(ctx.pathParam("message_id"));
           Message deletedMessage = messageService.deleteMessage(messageId);
          
           if (deletedMessage.getMessage_id() != 0) {
               // Message found and deleted successfully
               ctx.status(200);
               ctx.json(deletedMessage); // Return the deleted message in the response body
           } else {
               // Message not found
               ctx.status(200).result(""); // Empty response body
           }
       } catch (NumberFormatException e) {
           // Invalid message ID
           ctx.status(400);
           ctx.json("Invalid message ID");
       } catch (Exception e) {
           // Internal server error
           ctx.status(500);
           ctx.json("Error: " + e.getMessage());
       }
   }
  
   
  
   private void updateMessageHandler(Context ctx) {
    try {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message updatedMessage = ctx.bodyAsClass(Message.class);
        updatedMessage.setMessage_id(messageId);

        // Check if the message ID exists in the database
        Message existingMessage = messageService.getMessageById(messageId);
        if (existingMessage == null) {
            ctx.status(400);
            return;
        }

        // Check if the new message text is blank or too long
        if (updatedMessage.getMessage_text() == null || updatedMessage.getMessage_text().isEmpty() ||
                updatedMessage.getMessage_text().length() > 255) {
            ctx.status(400);
        }

        // Update the message
        Message result = messageService.updateMessage(updatedMessage);
        if (result != null) {
            ctx.json(result);
            ctx.status(200);
        } else {
            ctx.status(400).json("");
        }
    } catch (NumberFormatException e) {
        ctx.status(400).json("");
    } 
}


  
   private void getMessagesByUserIdHandler(Context ctx) {
       try {
           int accountId = Integer.parseInt(ctx.pathParam("account_id"));
           List<Message> messages = messageService.getMessagesByUserId(accountId);
           ctx.json(messages);
           ctx.status(200);
       } catch (NumberFormatException e) {
           ctx.status(400);
           ctx.json("Invalid account ID");
       } catch (Exception e) {
           ctx.status(500);
           ctx.json("Error: " + e.getMessage());
       }
   }


}
