package ru.xidv.andrst.sqlite.model;

/**
 * Created by Александр on 03.12.2017.
 */

public class Wallet {
    private int id;
    private double currentBalance;

    //    int status;
    private String walletName;
    private String note;

    public Wallet() {

    }

    public Wallet(String walletName, double currentBalance, String note) {
        this.note = note;
//        this.status = status;
        this.currentBalance = currentBalance;
        this.walletName = walletName;

    }

    public Wallet(int id, String walletName, double currentBalance, String note) {
        this.id = id;
        this.note = note;
//        this.status = status;
        this.currentBalance = currentBalance;
        this.walletName = walletName;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setNote(String note) {
        this.note = note;
    }


    // getters
    public long getId() {
        return this.id;
    }

    public String getNote() {
        return this.note;
    }

    public double getCurrentBalance() {
        return this.currentBalance;
    }

    public String getWalletName() {
        return this.walletName;
    }
}
