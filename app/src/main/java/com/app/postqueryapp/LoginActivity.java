package com.app.postqueryapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.postqueryapp.dto.Account;
import com.app.postqueryapp.progressBar.MProgressView;

import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * 登录活动，用于本地注册账号 和 检验账号、密码
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[100];
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;
    private View mLoginFormView;
    private View activity;

    // 是否记住账户、密码
    private CheckBox checkBoxAccount = null;
    private CheckBox checkBoxPassword = null;

    private ProgressDialog dialog = null;

    private int outProgress = 0;

    private Date startTime = null;

    private Date endTime = null;

    private boolean loginCancle = false;

    private boolean into = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();    // 获取系统自带的标题栏并隐藏，因为要使用自己定制的标题栏
        if(actionBar != null){
            actionBar.hide();
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);

        /**
         * 提取记住的账号、密码
         */
        checkBoxAccount = findViewById(R.id.checkBox_account);
        checkBoxPassword = findViewById(R.id.checkBox_password);
        try{
            SharedPreferences pref = getSharedPreferences("Memory", MODE_PRIVATE);
            checkBoxAccount.setChecked(pref.getBoolean("isMemoryAccount", false));
            checkBoxPassword.setChecked(pref.getBoolean("isMemoryPassword", false));
            if(pref.getBoolean("isMemoryAccount", false)){
                System.out.println("正在提取账号");
                mEmailView.setText(pref.getString("MemoryAccount",null));
                System.out.println("账号为" + pref.getString("MemoryAccount",null));
            }
            if(pref.getBoolean("isMemoryPassword", false)){
                System.out.println("正在提取密码");
                mPasswordView.setText(pref.getString("MemoryPassword",null));
                System.out.println("密码为" + pref.getString("MemoryPassword",null));
            }
        }catch (Exception e){
            System.out.println("取记录出错");
        }
        checkBoxPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxPassword.isChecked()){
                    checkBoxAccount.setChecked(checkBoxPassword.isChecked());
                }
            }
        });
        checkBoxAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!checkBoxAccount.isChecked()){
                    checkBoxPassword.setChecked(checkBoxAccount.isChecked());
                }
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        dialog.setTitle("登陆中");
        dialog.setMessage("请稍后......");
        // 监听cancel事件
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if(into){
                    into = false;
                }
                else{
                    loginCancle = true;
                }
            }
        });

//        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
//                new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialogg, int which) {
//                System.out.println("取消2222222222222222222222222222222222");
//                loginCancle = true;
//            }
//        });

        /**
         * 登录按钮事件，主要是从sqlLite数据库取出所注册的所有的account信息，检测是否匹配
         */
        final Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    List<Account> accountList = DataSupport.findAll(Account.class);

                    String email = mEmailView.getText().toString();
                    // 判断是否存在此用户的变量，不存在则拦截登录并提示，不然会闪退
                    boolean exit = false;
                    View focusView = null;
                    mEmailView.setError(null);
                    for(Account data:accountList){
                        if(data.getAuthor().equals(email)){
                            exit = true;
                        }
                    }

                    // 判断是否存在此用户，不存在则拦截登录并提示，不然会闪退
                    if(accountList.size() == 0 || !exit){
                        mEmailView.setError("该用户名不存在");
                        focusView = mEmailView;
                        focusView.requestFocus();
                        System.out.println("accountList为空或用户名不存在");
                    }
                    else {
                        for(int index = 0; index<accountList.size(); index++){
                            DUMMY_CREDENTIALS[index] = accountList.get(index).getAuthor() + ":" + accountList.get(index).getPassWord();
                        }
                        InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this.getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mEmailSignInButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        attemptLogin();
                    }
                }catch (Exception e){
                    System.out.println("登录出错");
                }


            }
        });

        /**
         * 注册账号按钮，获取用户和密码，设置Account类存入sqlLite数据库，同时校验数据的可靠性
         */
        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();

                mEmailView.setError(null);
                mPasswordView.setError(null);

                boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (password.contains(":")) {
                    mPasswordView.setError("密码中不能包含字符\":\"");
                    focusView = mPasswordView;
                    cancel = true;
                }

                if (!isPasswordValid(password)) {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    focusView = mPasswordView;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    mEmailView.setError("请填写");
                    focusView = mEmailView;
                    cancel = true;
                } else if (!isEmailValid(email)) {
                    mEmailView.setError("用户名应不小于6位");
                    focusView = mEmailView;
                    cancel = true;
                }
                else if(email.contains(":")){
                    mEmailView.setError("用户名中不能包含字符\":\"");
                    focusView = mEmailView;
                    cancel = true;
                }

                List<Account> accountList = DataSupport.findAll(Account.class);
                for(Account data:accountList){
                    if(data.getAuthor().equals(email)){
                        mEmailView.setError("该用户名已存在");
                        focusView = mEmailView;
                        cancel = true;
                    }
                }

                if(cancel){
                    focusView.requestFocus();
                }
                else{
                    Account account = new Account();
                    account.setAuthor(email);
                    account.setPassWord(password);
                    account.save();
                    // 弹出消息
                    Toast.makeText(LoginActivity.this, "您已注册成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(DUMMY_CREDENTIALS[0] == null){
            // 弹出消息
            Toast.makeText(LoginActivity.this, "请您先注册", Toast.LENGTH_SHORT).show();
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
            dialog.show();
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 5;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            try{
                if(loginCancle){
                    into = true;
                    dialog.cancel();
                    loginCancle = false;
                    System.out.println("赋予了");
                    System.out.println(loginCancle);
                }
                else if (success) {
                    loginCancle = false;
                    if(checkBoxAccount.isChecked()){
                        System.out.println("正在保存账号");
                        SharedPreferences.Editor editor = getSharedPreferences("Memory", MODE_PRIVATE).edit();
                        editor.putString("MemoryAccount", mEmailView.getText().toString());
                        editor.putBoolean("isMemoryAccount", checkBoxAccount.isChecked());
                        editor.apply();
                    } else{
                        System.out.println("正在取消保存账号");
                        SharedPreferences.Editor editor = getSharedPreferences("Memory", MODE_PRIVATE).edit();
                        editor.putBoolean("isMemoryAccount", checkBoxAccount.isChecked());
                        editor.apply();
                    }
                    if(checkBoxPassword.isChecked()){
                        System.out.println("正在保存密码");
                        SharedPreferences.Editor editor = getSharedPreferences("Memory", MODE_PRIVATE).edit();
                        editor.putString("MemoryPassword", mPasswordView.getText().toString());
                        editor.putBoolean("isMemoryPassword", checkBoxPassword.isChecked());
                        editor.apply();
                    } else{
                        System.out.println("正在取消保存密码");
                        SharedPreferences.Editor editor = getSharedPreferences("Memory", MODE_PRIVATE).edit();
                        editor.putBoolean("isMemoryPassword", checkBoxPassword.isChecked());
                        editor.apply();
                    }
//                 显式活动
                    Intent intent = new Intent(LoginActivity.this, MainActivitySecond.class);
                    startActivity(intent);
                }
//            else if(success && loginCancle){
//                System.out.println("取消3333333333333333333333333333333");
//                loginCancle = false;
//            }
                else {
                    into = true;
                    dialog.cancel();
                    loginCancle = false;
                    mPasswordView.setError(getString(R.string.error_incorrect_login));
                    mPasswordView.requestFocus();
                }
            }catch (Exception e){
                System.out.println("登录出错");
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            outProgress++;
            if(startTime != null && outProgress == 2){
                endTime = new Date();
                if(endTime.getTime() - startTime.getTime() < 1500){
                    ActivityController.finishAll();
                }
                else{
                    outProgress = 1;
                    startTime = null;
                    endTime = null;
                }
            }
            if(outProgress == 1){
                Toast.makeText(LoginActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                startTime = new Date();
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    return true;

                case KeyEvent.KEYCODE_MENU:
                    return true;

                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}

