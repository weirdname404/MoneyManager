package ru.xidv.andrst.moneymanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.xidv.andrst.sqlite.helper.DatabaseHelper;
import ru.xidv.andrst.sqlite.model.Expense;
import ru.xidv.andrst.sqlite.model.Income;
import ru.xidv.andrst.sqlite.model.Record;
import ru.xidv.andrst.sqlite.model.Wallet;

public class AddExpenseActivity extends Activity {

    int mCounter = 0;
    DatabaseHelper db;
    String hintButton = "Amount and Wallet values are required";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Button mSaveButton = findViewById(R.id.saveExpenseButton);
        Spinner mSourceSpinner = findViewById(R.id.expenseSourceSpinner);
        EditText mExpenseAmount = findViewById(R.id.editExpenseAmount);
        Spinner mWalletSpinner = findViewById(R.id.expenseWalletSpinner);
        EditText mNoteEditText = findViewById(R.id.expenseEditNote);

        mSaveButton.setEnabled(false);
        mSaveButton.setText(hintButton);

        mExpenseAmount.addTextChangedListener(expenseTextWatcher);

        db = new DatabaseHelper(getApplicationContext());

        final List<Wallet> allWallets = db.getAllWallets();

        final List<String> walletNames = new ArrayList<String>();

        for ( int i = 0; i < allWallets.size(); i++ ) {
            Wallet wallet = allWallets.get(i);

            walletNames.add(wallet.getWalletName());
            Log.e("Wallet Name", wallet.getWalletName());
        }


        ArrayAdapter<String> walletAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, walletNames);
        walletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWalletSpinner.setAdapter(walletAdapter);

        ArrayAdapter<CharSequence> sourceAdapter = ArrayAdapter.createFromResource(this,
                R.array.expenseSource, android.R.layout.simple_spinner_item);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSourceSpinner.setAdapter(sourceAdapter);
    }

    private TextWatcher expenseTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            mCounter++;
            dataCheck();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };

    @SuppressLint("SetTextI18n")
    public void dataCheck() {

        Button mSaveButton = findViewById(R.id.saveExpenseButton);
        EditText mExpenseAmount = findViewById(R.id.editExpenseAmount);
        Spinner mWalletSpinner = findViewById(R.id.expenseWalletSpinner);


        if (mCounter >= 1 && mWalletSpinner.getSelectedItem() != null) {
            boolean success = false;
            if (mExpenseAmount.getText().length() > 0) {
                try {
                    Double input = Double.parseDouble(mExpenseAmount.getText().toString());

                    if (mExpenseAmount.getText().length() >= 6) {

                        Context context = getApplicationContext();
                        CharSequence text = "Only 6 digits max";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }

                    if (input > -999999.99 && input < 999999.99) {
                        mSaveButton.setEnabled(true);
                        mSaveButton.setText("SAVE");
                    } else {
                        mSaveButton.setEnabled(false);
                        mSaveButton.setText(hintButton);
                    }

                } catch (Exception ignored) {
                }
            }
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.setText(hintButton);
        }
    }

    public void saveExpense(View view) {

        Spinner mSourceSpinner = findViewById(R.id.expenseSourceSpinner);
        EditText mExpenseAmount = findViewById(R.id.editExpenseAmount);
        Spinner mWalletSpinner = findViewById(R.id.expenseWalletSpinner);
        EditText mNoteEditText = findViewById(R.id.expenseEditNote);

        String chosenWalletName = mWalletSpinner.getSelectedItem().toString();
        String chosenSource = mSourceSpinner.getSelectedItem().toString();
        String userExpenseAmount = mExpenseAmount.getText().toString();
        String userExpenseNote = mNoteEditText.getText().toString();

        // Flurry Analytics gathering expense info
        Map<String, String> expenseParams = new HashMap<String, String>();
        expenseParams.put("Chosen wallet", chosenWalletName);
        expenseParams.put("Expense source", chosenSource);
        expenseParams.put("User expense amount ($)", userExpenseAmount);
        expenseParams.put("User expense note", userExpenseNote);
        FlurryAgent.logEvent("Expense was added", expenseParams);

        Expense expense = new Expense(chosenWalletName,
                chosenSource, Double.parseDouble(userExpenseAmount), userExpenseNote);

        long expense_id = db.createExpense(expense);

        Record expense_record = new Record(chosenWalletName, "",
                "", chosenSource,
                String.valueOf(Double.parseDouble(userExpenseAmount)), userExpenseNote);

        long expense_record_id = db.createExpenseRecord(expense_record);

        Wallet chosenWallet = db.getWallet(mWalletSpinner.getSelectedItemPosition() + 1);
        chosenWallet.setCurrentBalance(chosenWallet.getCurrentBalance() - Double.parseDouble(userExpenseAmount));
        db.updateWalletBalance(chosenWallet);

        db.closeDB();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
