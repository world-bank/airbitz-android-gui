/**
 * Copyright (c) 2014, Airbitz Inc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted provided that 
 * the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Redistribution or use of modified source code requires the express written
 *    permission of Airbitz Inc.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Airbitz Project.
 */

package com.airbitz.fragments.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.airbitz.R;
import com.airbitz.activities.NavigationActivity;
import com.airbitz.api.AirbitzAPI;
import com.airbitz.api.CoreAPI;
import com.airbitz.api.core;
import com.airbitz.api.tABC_CC;
import com.airbitz.api.tABC_Error;
import com.airbitz.fragments.BaseFragment;
import com.airbitz.fragments.settings.PasswordRecoveryFragment;
import com.airbitz.objects.HighlightOnPressButton;
import com.airbitz.utils.Common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 2/26/15.
 */
public class SetupUsernameFragment extends BaseFragment implements NavigationActivity.OnBackPress {

    private final String TAG = getClass().getSimpleName();

    public static String USERNAME = "com.airbitz.setupusername.username";

    private EditText mUserNameEditText;
    private HighlightOnPressButton mNextButton;
    private HighlightOnPressButton mBackButton;
    private TextView mTitleTextView;
    private View mUserNameRedRingCover;
    private CheckUsernameTask mCheckUsernameTask;
    private CoreAPI mCoreAPI;
    private View mView;
    private NavigationActivity mActivity;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCoreAPI = CoreAPI.getApi();
        mActivity = (NavigationActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setup_username, container, false);

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mUserNameRedRingCover = mView.findViewById(R.id.fragment_setup_username_redring);
        mUserNameRedRingCover.setVisibility(View.GONE);

        mTitleTextView = (TextView) mView.findViewById(R.id.layout_title_header_textview_title);
        mTitleTextView.setTypeface(NavigationActivity.montserratBoldTypeFace);
        mTitleTextView.setText(R.string.fragment_setup_titles);

        mBackButton = (HighlightOnPressButton) mView.findViewById(R.id.fragment_setup_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mNextButton = (HighlightOnPressButton) mView.findViewById(R.id.fragment_setup_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNext();
            }
        });

        mUserNameEditText = (EditText) mView.findViewById(R.id.fragment_setup_username_edittext);
        mUserNameEditText.setTypeface(NavigationActivity.helveticaNeueTypeFace);
        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (mUserNameEditText.getText().toString().length() < 3 || mUserNameEditText.getText().toString().trim().length() < 3) {
                    mUserNameRedRingCover.setVisibility(View.VISIBLE);
                } else {
                    mUserNameRedRingCover.setVisibility(View.GONE);
                    enableNextButton(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        mUserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mActivity.showSoftKeyboard(mUserNameEditText);
                }
            }
        });

        mUserNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(mUserNameRedRingCover.getVisibility() != View.VISIBLE) {
                        goNext();
                    }
                    return true;
                }
                return false;
            }
        });
        return mView;
    }

    private void enableNextButton(boolean enable) {
        if(enable) {
            mNextButton.setBackgroundResource(R.drawable.setup_button_green);
            mNextButton.setClickable(true);
        }
        else {
            mNextButton.setBackgroundResource(R.drawable.setup_button_dark_gray);
            mNextButton.setClickable(false);
        }
    }


    private void goNext() {
        mActivity.hideSoftKeyboard(mUserNameEditText);

        if (mCheckUsernameTask != null) {
            return;
        }

        // Reset errors.
        mUserNameEditText.setError(null);

        mCheckUsernameTask = new CheckUsernameTask();
        mCheckUsernameTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUserNameEditText.getText().toString());
    }

    private void launchSetupPassword() {
        SetupPasswordFragment fragment = new SetupPasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SetupPasswordFragment.USERNAME, mUserNameEditText.getText().toString());
        fragment.setArguments(bundle);
        mActivity.pushFragment(fragment);
    }

    @Override
    public boolean onBackPress() {
        mActivity.hideSoftKeyboard(getView());
        mActivity.noSignup();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        enableNextButton(false);
        if(bundle.containsKey(USERNAME)) {
            mUserNameEditText.setText(bundle.getString(USERNAME));
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mUserNameEditText.setSelection(mUserNameEditText.getText().length());
                mUserNameEditText.requestFocus();
            }
        });
    }

    /**
     * Determine if username available
     */
    public class CheckUsernameTask extends AsyncTask<String, Void, String> {
        CheckUsernameTask() {
            mActivity.showModalProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            return mCoreAPI.accountAvailable(username);
        }

        @Override
        protected void onPostExecute(String result) {
            onCancelled();
            if(result == null) {
                launchSetupPassword();
            }
            else {
                mActivity.ShowFadingDialog(result);
            }
        }

        @Override
        protected void onCancelled() {
            mCheckUsernameTask = null;
            mActivity.showModalProgress(false);
        }
    }
}
