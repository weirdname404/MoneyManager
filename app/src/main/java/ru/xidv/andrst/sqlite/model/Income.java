package ru.xidv.andrst.sqlite.model;

import java.util.Date;

/**
 * Created by Александр on 03.12.2017.
 */

public class Income {

    private int id;

    private String walletName;
    private String incomeSource;
    private double incomeAmount;

    private String note;
    private String date;

    public Income() {

    }

    public Income(String walletName, String incomeSource, double incomeAmount, String note) {
        this.note = note;
        this.incomeSource = incomeSource;
        this.walletName = walletName;
        this.incomeAmount = incomeAmount;

    }

    public Income(int id, String walletName, String incomeSource, double incomeAmount, String note) {
        this.id = id;
        this.note = note;
        this.incomeSource = incomeSource;
        this.walletName = walletName;
        this.incomeAmount = incomeAmount;
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

    public void setIncomeAmount(double incomeAmount) {
        this.incomeAmount = incomeAmount;
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

    public double getIncomeAmount() {
        return this.incomeAmount;
    }

    public String getIncomeNote() {
        return this.note;
    }

    public String getDate() {
        return this.date;
    }
}
