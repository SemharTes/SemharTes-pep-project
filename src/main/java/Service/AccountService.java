package Service;




import DAO.AccountDAO;
import Model.Account;


public class AccountService {
   private final AccountDAO accountDAO;


   public AccountService() {
       accountDAO = new AccountDAO();
   }


   public AccountService(AccountDAO accountDAO) {
       this.accountDAO = accountDAO;
   }


public Account registerAccount(Account account) {
    // Validate username and password
    if (!isValidAccount(account)) {
        throw new IllegalArgumentException("");
    }

    // Check if the username already exists
    Account existingAccount = accountDAO.getAccountByUsername(account.getUsername());
    if (existingAccount != null) {
        throw new IllegalArgumentException("");
    }

    // If the username does not exist and the account is valid
    return accountDAO.createAccount(account);
}



   public Account login(String username, String password) {
       return accountDAO.login(username, password);
   }


   
   private boolean isValidAccount(Account account) {
       return account != null && !account.getUsername().isEmpty() && account.getPassword().length() >= 4;
   }


   public boolean doesUserExist(int userId) {
       Account account = accountDAO.getAccountById(userId);
       return account != null;
   }
}
