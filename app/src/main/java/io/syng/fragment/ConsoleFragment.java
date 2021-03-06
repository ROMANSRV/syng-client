/*
 * Copyright (c) 2015 Jarrad Hope
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.syng.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.leakcanary.RefWatcher;

import org.ethereum.android.service.ConnectorHandler;
import org.ethereum.android.service.events.EventFlag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.UUID;

import io.syng.R;
import io.syng.app.SyngApplication;


public class ConsoleFragment extends Fragment implements OnClickListener {

    private static final String ACTION_RECEIVE = "dapp://syng.io/dapps/wallet/#/tab/receive";
    private static final String ACTION_SEND = "dapp://syng.io/dapps/wallet/#/tab/send/";

    private final static int CONSOLE_LENGTH = 10000;
    private final static int CONSOLE_REFRESH_MILLS = 1000 * 5; //5 sec

    private TextView mConsoleText;

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            int length = SyngApplication.mConsoleLog.length();
            if (length > CONSOLE_LENGTH) {
                SyngApplication.mConsoleLog = SyngApplication.mConsoleLog.substring(CONSOLE_LENGTH * ((length / CONSOLE_LENGTH) - 1) + length % CONSOLE_LENGTH);
            }
            mConsoleText.setText(SyngApplication.mConsoleLog);

            mHandler.postDelayed(mRunnable, CONSOLE_REFRESH_MILLS);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_console, container, false);

        mConsoleText = (TextView) view.findViewById(R.id.tv_console_log);
        mConsoleText.setText(SyngApplication.mConsoleLog);
        mConsoleText.setMovementMethod(new ScrollingMovementMethod());

        ImageView background = (ImageView) view.findViewById(R.id.iv_background);
        Glide.with(this).load(R.drawable.console_bg).into(background);

        view.findViewById(R.id.fab_send).setOnClickListener(this);
        view.findViewById(R.id.fab_receive).setOnClickListener(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = SyngApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_send:
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(ACTION_SEND));
                if (intent1.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent1);
                }
                break;
            case R.id.fab_receive:
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse(ACTION_RECEIVE));
                if (intent2.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent2);
                }
                break;
        }
    }
}
