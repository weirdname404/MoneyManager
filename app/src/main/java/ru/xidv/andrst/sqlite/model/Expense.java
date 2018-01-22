package ru.xidv.andrst.sqlite.model;

/**
 * Created by Александр on 03.12.2017.
 */

public class Expense {


    private int id;

    private String walletName;
    private String expenseSource;
    private double expenseAmount;

    private String note;
    private String date;

    public Expense() {

    }

    public Expense(String walletName, String expenseSource, double expenseAmount, String note) {
        this.note = note;
        this.expenseSource = expenseSource;
        this.walletName = walletName;
        this.expenseAmount = expenseAmount;

    }

    public Expense(int id, String walletName, String expenseSource, double expenseAmount, String note) {
        this.id = id;
        this.note = note;
        this.expenseSource = expenseSource;
        this.walletName = walletName;
        this.expenseAmount = expenseAmount;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setExpenseSource(String expenseSource) {
        this.expenseSource = expenseSource;
    }

    public void setExpenseAmount(double expenseAmount) {
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

    public String getExpenseSource() {
        return this.expenseSource;
    }

    public double getExpenseAmount() {
        return this.expenseAmount;
    }

    public String getNote() {
        return this.note;
    }

    public String getDate() {
        return this.date;
    }
}
