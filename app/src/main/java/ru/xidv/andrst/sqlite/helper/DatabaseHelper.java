package ru.xidv.andrst.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.xidv.andrst.sqlite.model.Expense;
import ru.xidv.andrst.sqlite.model.Income;
import ru.xidv.andrst.sqlite.model.Record;
import ru.xidv.andrst.sqlite.model.Wallet;

/**
 * Created by Александр on 03.12.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "moneyManager";

    // Table Names
    private static final String TABLE_WALLET = "WALLET";
    private static final String TABLE_INCOME = "INCOME";
    private static final String TABLE_EXPENSE = "EXPENSE";
    private static final String TABLE_WALLET_HISTORY = "WALLET_HISTORY";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_WALLET_NAME = "wallet_name";
    private static final String KEY_NOTE = "note";
    private static final String KEY_DATE = "date";

    // WALLET Table - column name

    private static final String KEY_CURRENT_BALANCE = "current_balance";
    private static final String KEY_STATUS = "status";

    // INCOME Table - column name

    private static final String KEY_INCOME_AMOUNT = "income_amount";
    private static final String KEY_INCOME_SOURCE = "income_source";
//    private static final String KEY_INCOME_NOTE = "income_note";

    // EXPENSE Table - column name

    private static final String KEY_EXPENSE_AMOUNT = "expense_amount";
    private static final String KEY_EXPENSE_SOURCE = "expense_source";
//    private static final String KEY_EXPENSE_NOTE = "expense_note";


    // Table Create Statements
    // WALLET table create statement
    private static final String CREATE_TABLE_WALLET = "CREATE TABLE "
            + TABLE_WALLET + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WALLET_NAME
            + " TEXT," + KEY_CURRENT_BALANCE + " NUMERIC," + KEY_NOTE + " TEXT," + KEY_STATUS
            + " INTEGER" + ")";

    // INCOME table create statement
    private static final String CREATE_TABLE_INCOME = "CREATE TABLE "
            + TABLE_INCOME + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INCOME_SOURCE
            + " TEXT," + KEY_INCOME_AMOUNT + " NUMERIC," + KEY_WALLET_NAME + " TEXT," + KEY_DATE
            + " DATETIME," + KEY_NOTE + " TEXT," + KEY_STATUS
            + " INTEGER" + ")";

    // EXPENSE table create statement
    private static final String CREATE_TABLE_EXPENSE = "CREATE TABLE "
            + TABLE_EXPENSE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EXPENSE_SOURCE
            + " TEXT," + KEY_EXPENSE_AMOUNT + " NUMERIC," + KEY_WALLET_NAME + " TEXT," + KEY_DATE
            + " DATETIME," + KEY_NOTE + " TEXT," + KEY_STATUS
            + " INTEGER" + ")";

    // WALLET_HISTORY table create statement
    private static final String CREATE_TABLE_WALLET_HISTORY = "CREATE TABLE "
            + TABLE_WALLET_HISTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EXPENSE_SOURCE
            + " TEXT," + KEY_EXPENSE_AMOUNT + " TEXT," + KEY_WALLET_NAME + " TEXT," + KEY_DATE
            + " DATETIME," + KEY_INCOME_SOURCE + " TEXT," + KEY_INCOME_AMOUNT
            + " TEXT," + KEY_NOTE + " TEXT," + KEY_STATUS
            + " INTEGER" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_WALLET);
        db.execSQL(CREATE_TABLE_INCOME);
        db.execSQL(CREATE_TABLE_EXPENSE);
        db.execSQL(CREATE_TABLE_WALLET_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLET_HISTORY);

        // create new tables
        onCreate(db);

    }


    /**
     * Creating a wallet
     */
    public long createWallet(Wallet wallet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WALLET_NAME, wallet.getWalletName());
        values.put(KEY_CURRENT_BALANCE, wallet.getCurrentBalance());
        values.put(KEY_NOTE, wallet.getNote());

        // insert row
        return db.insert(TABLE_WALLET, null, values);
    }

    /**
     * get single wallet
     */
    public Wallet getWallet(long wallet_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_WALLET + " WHERE "
                + KEY_ID + " = " + wallet_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Wallet wt = new Wallet();
        wt.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        wt.setNote((c.getString(c.getColumnIndex(KEY_NOTE))));
        wt.setWalletName(c.getString(c.getColumnIndex(KEY_WALLET_NAME)));
        wt.setCurrentBalance(c.getDouble(c.getColumnIndex(KEY_CURRENT_BALANCE)));

        return wt;
    }

    /**
     * Creating an income
     */
    public long createIncome(Income income) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INCOME_SOURCE, income.getIncomeSource());
        values.put(KEY_INCOME_AMOUNT, income.getIncomeAmount());
        values.put(KEY_NOTE, income.getIncomeNote());
        values.put(KEY_WALLET_NAME, income.getWalletName());
        values.put(KEY_DATE, getDateTime());

        // insert row
        return db.insert(TABLE_INCOME, null, values);
    }

    public long createIncomeRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_INCOME_SOURCE, record.getIncomeSource());
        values.put(KEY_INCOME_AMOUNT, String.valueOf(record.getIncomeAmount()));
        values.put(KEY_NOTE, record.getRecordNote());
        values.put(KEY_WALLET_NAME, record.getWalletName());
        values.put(KEY_DATE, getDateTime());

        // insert row
        return db.insert(TABLE_WALLET_HISTORY, null, values);
    }


    /**
     * Creating an expense
     */
    public long createExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXPENSE_SOURCE, expense.getExpenseSource());
        values.put(KEY_EXPENSE_AMOUNT, expense.getExpenseAmount());
        values.put(KEY_NOTE, expense.getNote());
        values.put(KEY_WALLET_NAME, expense.getWalletName());
        values.put(KEY_DATE, getDateTime());

        // insert row
        return db.insert(TABLE_EXPENSE, null, values);
    }

    public long createExpenseRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXPENSE_SOURCE, record.getExpenseSource());
        values.put(KEY_EXPENSE_AMOUNT, String.valueOf(record.getExpenseAmount()));
        values.put(KEY_NOTE, record.getRecordNote());
        values.put(KEY_WALLET_NAME, record.getWalletName());
        values.put(KEY_DATE, getDateTime());

        // insert row
        return db.insert(TABLE_WALLET_HISTORY, null, values);
    }


    /**
     * getting all wallets
     */
    public List<Wallet> getAllWallets() {
        ArrayList<Wallet> walletArrayList = new ArrayList<Wallet>();
        String selectQuery = "SELECT * FROM " + TABLE_WALLET;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Wallet wt = new Wallet();

                wt.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                wt.setWalletName(c.getString(c.getColumnIndex(KEY_WALLET_NAME)));
                wt.setNote((c.getString(c.getColumnIndex(KEY_NOTE))));
                wt.setCurrentBalance(c.getDouble(c.getColumnIndex(KEY_CURRENT_BALANCE)));

                walletArrayList.add(wt);
            } while (c.moveToNext());
        }

        return walletArrayList;
    }

    /**
     * getting all incomes
     */
    public List<Income> getAllIncomes() {
        ArrayList<Income> all_Incomes = new ArrayList<Income>();
        String selectQuery = "SELECT * FROM " + TABLE_INCOME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Income income = new Income();

                income.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                income.setWalletName(c.getString(c.getColumnIndex(KEY_WALLET_NAME)));
                income.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                income.setIncomeSource(c.getString(c.getColumnIndex(KEY_INCOME_SOURCE)));
                income.setIncomeAmount(c.getDouble(c.getColumnIndex(KEY_INCOME_AMOUNT)));
                income.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                all_Incomes.add(income);

            } while (c.moveToNext());
        }

        return all_Incomes;
    }

    /**
     * getting all expenses
     */
    public List<Expense> getAllExpenses() {
        ArrayList<Expense> all_Expenses = new ArrayList<Expense>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Expense expense = new Expense();

                expense.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                expense.setWalletName(c.getString(c.getColumnIndex(KEY_WALLET_NAME)));
                expense.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                expense.setExpenseSource(c.getString(c.getColumnIndex(KEY_EXPENSE_SOURCE)));
                expense.setExpenseAmount(c.getDouble(c.getColumnIndex(KEY_EXPENSE_AMOUNT)));
                expense.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                all_Expenses.add(expense);

            } while (c.moveToNext());
        }

        return all_Expenses;
    }

    /**
     * getting all records
     */
    public List<Record> getAllWalletRecords() {
        ArrayList<Record> all_Records = new ArrayList<Record>();
        String selectQuery = "SELECT * FROM " + TABLE_WALLET_HISTORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Record record = new Record();

                record.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                record.setWalletName(c.getString(c.getColumnIndex(KEY_WALLET_NAME)));
                record.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                record.setExpenseSource(c.getString(c.getColumnIndex(KEY_EXPENSE_SOURCE)));
                record.setExpenseAmount(c.getString(c.getColumnIndex(KEY_EXPENSE_AMOUNT)));
                record.setIncomeSource(c.getString(c.getColumnIndex(KEY_INCOME_SOURCE)));
                record.setIncomeAmount(c.getString(c.getColumnIndex(KEY_INCOME_AMOUNT)));
                record.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                all_Records.add(record);

            } while (c.moveToNext());
        }

        return all_Records;
    }


    /**
     * Updating a wallet Balance
     */
    public int updateWalletBalance(Wallet wallet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CURRENT_BALANCE, wallet.getCurrentBalance());

        // updating row
        return db.update(TABLE_WALLET, values, KEY_ID + " = ?",
                new String[]{String.valueOf(wallet.getId())});
    }

    /**
     * getting wallet count
     */
    public int getCount(String TABLE) {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /**
     * Deleting data
     */
    public void delete(String TABLE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE);

    }

    /**
     * get datetime
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm dd-MM", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
