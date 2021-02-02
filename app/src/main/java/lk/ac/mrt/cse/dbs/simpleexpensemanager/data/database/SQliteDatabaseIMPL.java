package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQliteDatabaseIMPL extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "180525M";

    public static final String TABLE_NAME_ACCOUNT = "account_table";
    public static final String COLUMN_ACCOUNT_NO = "accountNo";
    public static final String COLUMN_BANK_NAME = "bankName";
    public static final String COLUMN_ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final String COLUMN_BALANCE = "balance";

    public static final String TABLE_NAME_TRANSACTION = "transaction_table";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_EXPENSE_TYPE = "expenseType";
    public static final String COLUMN_AMOUNT = "amount";


    public SQliteDatabaseIMPL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqDatabase) {
        String TABLE_CREATE_ACCOUNT ="CREATE TABLE " + TABLE_NAME_ACCOUNT+ " ("
                +COLUMN_ACCOUNT_NO+ " TEXT(50) ,"
                +COLUMN_BANK_NAME+ " TEXT(50) ,"
                +COLUMN_ACCOUNT_HOLDER_NAME+ " TEXT(50) NOT NULL,"
                +COLUMN_BALANCE+ " REAL NOT NULL , PRIMARY KEY("+COLUMN_ACCOUNT_NO+")"
                +" )";

        String TABLE_CREATE_TRANSACTION ="CREATE TABLE " + TABLE_NAME_TRANSACTION+
                " ("
                +COLUMN_DATE+ " date  ,"
                +COLUMN_ACCOUNT_NO+ " TEXT(50)  ,"
                +COLUMN_EXPENSE_TYPE+ " TEXT(20) ,"
                +COLUMN_AMOUNT+ " REAL ,FOREIGN KEY ("+COLUMN_ACCOUNT_NO+") REFERENCES "+TABLE_NAME_ACCOUNT+"(" +COLUMN_ACCOUNT_NO+ ") ON UPDATE CASCADE)";

        sqDatabase.execSQL(TABLE_CREATE_ACCOUNT);
        sqDatabase.execSQL(TABLE_CREATE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqDatabase, int i, int i1) {
        sqDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_ACCOUNT);
        sqDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_TRANSACTION);
        onCreate(sqDatabase);
    }

    public ArrayList<Account> getAccountList(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Account> accountArray=new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME_ACCOUNT,null);
        if(res.getCount()!=0) {
            while (res.moveToNext()) {
                String accountNo = res.getString(0);
                String bankName = res.getString(1);
                String accountHolderName = res.getString(2);
                double balance = res.getDouble(3);
                accountArray.add(new Account(accountNo, bankName, accountHolderName, balance));
            }
        }
        return accountArray;
    }

    public ArrayList<String> getAccountNumberList(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> accountNoArray=new ArrayList<>();
        Cursor res = db.rawQuery("SELECT accountNo FROM "+TABLE_NAME_ACCOUNT,null);
        if(res.getCount()!=0) {
            while (res.moveToNext()) {
                String accountNo = res.getString(0);
                accountNoArray.add(accountNo);
            }
        }
        return accountNoArray;

    }

    public Account getAccount(String accNo){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME_ACCOUNT+" WHERE accountNo = ?",new String[]{accNo});
        Account account = null;
        if(res.getCount() != 0){
            while(res.moveToNext()){
                String accountNo = res.getString(0);
                String bankName = res.getString(1);
                String accountHolderName = res.getString(2);
                double balance = res.getDouble(3);
                account = new Account(accountNo,bankName,accountHolderName,balance);
            }
        }
        return account;
    }

    public boolean AddAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bankName",account.getBankName());
        contentValues.put("accountHolderName",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        long result = db.insert(TABLE_NAME_ACCOUNT,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean RemoveAccount(String accNo){
        SQLiteDatabase db = this.getWritableDatabase();
        long result =  db.delete(TABLE_NAME_ACCOUNT,"accountNo = "+accNo,null);
        if(result>0) return true;
        else return false;

    }

    public boolean updateBalance(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        long result = db.update(TABLE_NAME_ACCOUNT, contentValues, "accountNo = ?", new String[]{account.getAccountNo()});
        if (result > 0) return true;
        else return false;
    }

    //Queries for Transaction
    public boolean logTransaction(Transaction transaction){
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",format.format(transaction.getDate()));
        contentValues.put("accountNo",transaction.getAccountNo());
        contentValues.put("expenseType",transaction.getExpenseType().toString());
        contentValues.put("amount",transaction.getAmount());
        long result = db.insert(TABLE_NAME_TRANSACTION,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }


    public ArrayList<Transaction> getAllTransactionLogs() throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        ArrayList<Transaction> transactionArray=new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_TRANSACTION,null);
        if(res.getCount()!=0) {
            while (res.moveToNext()) {
                Date date = new Date();
                try {
                    date = format.parse(res.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = res.getString(1);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                double amount = res.getDouble(3);
                transactionArray.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        return transactionArray;
    }

    public ArrayList<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        ArrayList<Transaction> transactionArray=new ArrayList<>();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME_TRANSACTION+" LIMIT "+limit,null);
        if(res.getCount()!=0) {
            while (res.moveToNext()) {
                Date date = new Date();
                try {
                    date = format.parse(res.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = res.getString(1);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                double amount = res.getDouble(3);
                transactionArray.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        return transactionArray;
    }


}
