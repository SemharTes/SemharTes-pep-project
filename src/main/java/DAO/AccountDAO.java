package DAO;


import Model.Account;
import Util.ConnectionUtil;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AccountDAO {


   public Account createAccount(Account account) {
       String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
           statement.setString(1, account.getUsername());
           statement.setString(2, account.getPassword());
           int affectedRows = statement.executeUpdate();
           if (affectedRows == 0) {
               throw new SQLException("Creating account failed, no rows affected.");
           }
           try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
               if (generatedKeys.next()) {
                   account.setAccount_id(generatedKeys.getInt(1));
               } else {
                   throw new SQLException("Creating account failed, no ID obtained.");
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return account;
   }


   public Account getAccountByUsername(String username) {
       String sql = "SELECT * FROM account WHERE username = ?";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)) {
           statement.setString(1, username);
           try (ResultSet resultSet = statement.executeQuery()) {
               if (resultSet.next()) {
                   return new Account(resultSet.getInt("account_id"),
                           resultSet.getString("username"),
                           resultSet.getString("password"));
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return null;
   }


   public Account getAccountById(int userId) {
       String sql = "SELECT * FROM account WHERE account_id = ?";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)) {
           statement.setInt(1, userId);
           try (ResultSet resultSet = statement.executeQuery()) {
               if (resultSet.next()) {
                   return new Account(
                           resultSet.getInt("account_id"),
                           resultSet.getString("username"),
                           resultSet.getString("password")
                   );
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return null;
   }


   public List<Account> getAllAccounts() {
       List<Account> accounts = new ArrayList<>();
       String sql = "SELECT * FROM account";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
           while (resultSet.next()) {
               accounts.add(new Account(resultSet.getInt("account_id"),
                       resultSet.getString("username"),
                       resultSet.getString("password")));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return accounts;
   }




   public Account login(String username, String password) {
       String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
       try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
           stmt.setString(1, username);
           stmt.setString(2, password);
           try (ResultSet rs = stmt.executeQuery()) {
               if (rs.next()) {
                   int id = rs.getInt("account_id");
                //    String dbUsername = rs.getString("username");
                //    String dbPassword = rs.getString("password");
                //    System.out.println("Database username: " + dbUsername);
                //    System.out.println("Database password: " + dbPassword);
                   return new Account(id, username, password);
               }
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return null;
   }
}
