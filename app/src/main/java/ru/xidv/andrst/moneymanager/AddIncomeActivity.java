package ru.xidv.andrst.moneymanager;

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
import ru.xidv.andrst.sqlite.model.Income;
import ru.xidv.andrst.sqlite.model.Record;
import ru.xidv.andrst.sqlite.model.Wallet;

public class AddIncomeActivity extends Activity {

    int mCounter = 0;
    DatabaseHelper db;
    String hintButton = "Amount and Wallet values are required";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        Button mSaveButton = findViewById(R.id.saveIncomeButton);
        Spinner mSourceSpinner = findViewById(R.id.sourceSpinner);
        EditText mIncomeAmount = findViewById(R.id.editIncomeAmount);
        Spinner mWalletSpinner = findViewById(R.id.walletSpinner);
        EditText mNoteEditText = findViewById(R.id.editNote);

        mSaveButton.setEnabled(false);
        mSaveButton.setText(hintButton);

        mIncomeAmount.addTextChangedListener(incomeTextWatcher);


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
                R.array.incomeSource, android.R.layout.simple_spinner_item);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSourceSpinner.setAdapter(sourceAdapter);
    }

    private TextWatcher incomeTextWatcher = new TextWatcher() {

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

    public void dataCheck() {

        Button mSaveButton = findViewById(R.id.saveIncomeButton);
        EditText mIncomeAmount = findViewById(R.id.editIncomeAmount);
        Spinner mWalletSpinner = findViewById(R.id.walletSpinner);


        if (mCounter >= 1 && mWalletSpinner.getSelectedItem() != null) {
            boolean success = false;
            if (mIncomeAmount.getText().length() > 0) {
                try {
                    Double input = Double.parseDouble(mIncomeAmount.getText().toString());

                    if (mIncomeAmount.getText().length() >= 6) {

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

    public void saveIncome(View view) {

        Spinner mSourceSpinner = findViewById(R.id.sourceSpinner);
        EditText mIncomeAmount = findViewById(R.id.editIncomeAmount);
        Spinner mWalletSpinner = findViewById(R.id.walletSpinner);
        EditText mNoteEditText = findViewById(R.id.editNote);

        String chosenWalletName = mWalletSpinner.getSelectedItem().toString();
        String chosenSource = mSourceSpinner.getSelectedItem().toString();
        String userIncomeAmount = mIncomeAmount.getText().toString();
        String userIncomeNote = mNoteEditText.getText().toString();

        // Flurry Analytics gathering income info
        Map<String, String> incomeParams = new HashMap<String, String>();
        incomeParams.put("Chosen wallet", chosenWalletName);
        incomeParams.put("Income source", chosenSource);
        incomeParams.put("User income amount ($)", userIncomeAmount);
        incomeParams.put("User income note", userIncomeNote);
        FlurryAgent.logEvent("Income was added", incomeParams);

        Income income = new Income(chosenWalletName,
                chosenSource, Double.parseDouble(userIncomeAmount), userIncomeNote);


        long income_id = db.createIncome(income);

        Record income_record = new Record(chosenWalletName, chosenSource,
                String.valueOf(Double.parseDouble(userIncomeAmount)), "", "", userIncomeNote);

        long income_record_id = db.createIncomeRecord(income_record);

        Wallet chosenWallet = db.getWallet(mWalletSpinner.getSelectedItemPosition() + 1);
        chosenWallet.setCurrentBalance(chosenWallet.getCurrentBalance() + Double.parseDouble(userIncomeAmount));
        db.updateWalletBalance(chosenWallet);

        db.closeDB();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
