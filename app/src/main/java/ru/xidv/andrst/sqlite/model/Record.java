package ru.xidv.andrst.sqlite.model;

/**
 * Created by Александр on 06.12.2017.
 */

public class Record {

    private int id;

    private String walletName;

    private String incomeSource;
    private String incomeAmount;

    private String expenseSource;
    private String expenseAmount;

    private String note;
    private String date;

    public Record() {

    }

    public Record(String walletName, String incomeSource, String incomeAmount,
                  String expenseSource, String expenseAmount, String note) {
        this.walletName = walletName;
        this.incomeSource = incomeSource;
        this.incomeAmount = incomeAmount;
        this.note = note;

        this.expenseSource = expenseSource;
        this.expenseAmount = expenseAmount;

    }

    public Record(int id, String walletName, String incomeSource, String incomeAmount,
                  String expenseSource, String expenseAmount, String note) {
        this.id = id;
        this.walletName = walletName;
        this.incomeSource = incomeSource;
        this.incomeAmount = incomeAmount;
        this.note = note;

        this.expenseSource = expenseSource;
        this.expenseAmount = expenseAmount;

    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    public void setIncomeAmount(String incomeAmount) {
        this.incomeAmount = incomeAmount;
    }


    public void setExpenseSource(String expenseSource) {
        this.expenseSource = expenseSource;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // getter
    public int getId() {
        return this.id;
    }

    public String getWalletName() {
        return this.walletName;
    }

    public String getIncomeSource() {
        return this.incomeSource;
    }

    public String getIncomeAmount() {
        return this.incomeAmount;
    }

    public String getExpenseSource() {
        return this.expenseSource;
    }

    public String getExpenseAmount() {
        return this.expenseAmount;
    }

    public String getRecordNote() {
        return this.note;
    }

    public String getDate() {
        return this.date;
    }
}
