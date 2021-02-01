package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.SQliteDatabaseIMPL;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class PersistentAccountDAO implements AccountDAO {
    //private final Map<String, Account> accounts;
    SQliteDatabaseIMPL databaseHelper;

    public PersistentAccountDAO(Context context){
        databaseHelper = new SQliteDatabaseIMPL(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return  databaseHelper.getAccountNumberList();
    }

    @Override
    public List<Account> getAccountsList() {
        return  databaseHelper.getAccountList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account acc = databaseHelper.getAccount(accountNo);
        if (acc!=null) {
            return acc;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) throws InvalidAccountException {
        Boolean added;
        Account acc= databaseHelper.getAccount(account.getAccountNo());
        if(acc==null){
            databaseHelper.AddAccount(account);
        }
        else{
            if(acc.getBankName()!=account.getBankName()){
                databaseHelper.AddAccount(account);
            }
            else{
                String msg = "Account " + acc + " is invalid.";
                throw new InvalidAccountException(msg);
            }
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Boolean deleted = databaseHelper.RemoveAccount(accountNo);
        if (deleted == false) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = this.getAccount(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                if(account.getBalance()>=amount){
                    account.setBalance(account.getBalance()-amount);
                }
                break;
            case INCOME:
                account.setBalance(account.getBalance()+amount);
                break;
        }
        Boolean updated = databaseHelper.updateBalance(account);
        if (updated==false) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}
