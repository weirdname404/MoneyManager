package ru.xidv.andrst.moneymanager;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ru.xidv.andrst.sqlite.helper.DatabaseHelper;
import ru.xidv.andrst.sqlite.model.Record;
import ru.xidv.andrst.sqlite.model.Wallet;


public class MainActivity extends AppCompatActivity {

    // Database Helper
    DatabaseHelper db;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "KMRF39RV9ZPN7C6QYN3N");

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        db = new DatabaseHelper(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        db.closeDB();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void addWallet(View view) {
        Intent intent = new Intent(getApplicationContext(), AddWalletActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                System.out.println("success");
                this.recreate();
            }
        }
    }

    public void addIncome(View view) {
        Intent intent = new Intent(getApplicationContext(), AddIncomeActivity.class);
        startActivityForResult(intent, 1);
    }

    public void addExpense(View view) {

        Intent intent = new Intent(getApplicationContext(), AddExpenseActivity.class);
        startActivityForResult(intent, 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_wallets) {

            List<Wallet> WalletArrayList = db.getAllWallets();

            if (WalletArrayList.size() > 0) {
                db = new DatabaseHelper(getApplicationContext());
                db.delete("WALLET");
                db.closeDB();

                showToast("All WALLETS were deleted");

                FlurryAgent.logEvent("User deleted all WALLETS");

                this.recreate();
                return true;
            } else {
                showToast("At first, create a wallet");
            }

        }

        if (id == R.id.action_delete_history) {

            List<Record> RecordArrayList = db.getAllWalletRecords();

            if (RecordArrayList.size() > 0) {
                db = new DatabaseHelper(getApplicationContext());
                db.delete("INCOME");
                db.delete("EXPENSE");
                db.delete("WALLET_HISTORY");
                db.closeDB();

                FlurryAgent.logEvent("User deleted HISTORY");

                showToast("All HISTORY was deleted");

                this.recreate();
                return true;
            } else {
                showToast("At first, make a note about lost or received money");
            }

        }


        if (id == R.id.action_show_description) {

            List<Wallet> walletArrayList = db.getAllWallets();

            if (walletArrayList.size() > 0) {
                Spinner walletSpinner = findViewById(R.id.spinner);

                db = new DatabaseHelper(getApplicationContext());

                showToast(walletSpinner.getSelectedItem()
                        + ":\n" + walletArrayList.get(walletSpinner.getSelectedItemPosition()).getNote());

                db.closeDB();
            } else {
                showToast("Create a wallet and write there a note");
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return new BalanceFragment();

                case 1:

                    return new HistoryFragment();

                case 2:
                    return new ChartFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
//             Show 3 total pages.

            return 3;
        }
    }
}
