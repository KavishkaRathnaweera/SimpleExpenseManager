package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

        import android.content.Context;

        import java.text.ParseException;
        import
        java.util.Date;
        import java.util.LinkedList;
        import java.util.List;

        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.SQliteDatabaseIMPL;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
        import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    //private final List<Transaction> transactions;
    SQliteDatabaseIMPL databaseHelper;
    PersistentAccountDAO persistantAccount;

    public PersistentTransactionDAO(Context context) {
        databaseHelper = new SQliteDatabaseIMPL(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Account acc = databaseHelper.getAccount(accountNo);
        switch (expenseType) {
            case EXPENSE:
                if(acc.getBalance()>=amount){
                    Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                    databaseHelper.logTransaction(transaction);
                }
                break;
            case INCOME:
                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                databaseHelper.logTransaction(transaction);
                break;
        }

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        try {
            return databaseHelper.getAllTransactionLogs();
        } catch (ParseException e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        // return the last <code>limit</code> number of transaction logs
        return databaseHelper.getPaginatedTransactionLogs(limit);
    }

}

