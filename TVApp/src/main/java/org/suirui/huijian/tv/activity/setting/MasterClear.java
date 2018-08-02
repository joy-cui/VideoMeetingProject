/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.suirui.huijian.tv.activity.setting;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.service.persistentdata.PersistentDataBlockManager;
import com.android.internal.os.storage.ExternalStorageFormatter;

import android.content.Intent;

import org.suirui.huijian.tv.R;

/**
 * Confirm and execute a reset of the device to a clean "just out of the box"
 * state.  Multiple confirmations are required: first, a general "are you sure
 * you want to do this?" prompt, followed by a keyguard pattern trace if the user
 * has defined one, followed by a final strongly-worded "THIS WILL ERASE EVERYTHING
 * ON THE PHONE" prompt.  If at any time the phone is allowed to go to sleep, is
 * locked, et cetera, then the confirmation sequence is abandoned.
 *
 * This is the confirmation screen.
 */
public class MasterClear{

    private boolean mEraseSdCard = true;
    public static final String M_PERSISTENT_DATA_BLOCK_SERVICE = "persistent_data_block";
    public static final String EXTRA_REASON = "android.intent.extra.REASON";
    public static final String ACTION_MASTER_CLEAR = "android.intent.action.MASTER_CLEAR";

    private static MasterClear instance = null;

    private MasterClear() {
    }

    public static synchronized MasterClear getInstance() {
        if (instance == null) {
            instance = new MasterClear();
        }

        return instance;
    }

    /**
     * The user has gone through the multiple confirmation, so now we go ahead
     * and invoke the Checkin Service to reset the device to its factory-default
     * state (rebooting in the process).
     */
        public void deviceResume(final Activity activity) {
            if (isMonkeyRunning()) {
                return;
            }

            final PersistentDataBlockManager pdbManager = (PersistentDataBlockManager)
                    activity.getSystemService(M_PERSISTENT_DATA_BLOCK_SERVICE);

            if (pdbManager != null && !pdbManager.getOemUnlockEnabled()) {
                // if OEM unlock is enabled, this will be wiped during FR process.
                final ProgressDialog progressDialog = getProgressDialog(activity);
                progressDialog.show();

                // need to prevent orientation changes as we're about to go into
                // a long IO request, so we won't be able to access inflate resources on flash
                final int oldOrientation = activity.getRequestedOrientation();
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        pdbManager.wipe();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        progressDialog.hide();
                        activity.setRequestedOrientation(oldOrientation);
                        doMasterClear(activity);
                    }
                }.execute();
            } else {
                doMasterClear(activity);
            }
        }



    private ProgressDialog getProgressDialog(Activity activity) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(
                activity.getString(R.string.master_clear_progress_title));
        progressDialog.setMessage(
                activity.getString(R.string.master_clear_progress_text));
        return progressDialog;
    }

    private void doMasterClear(Activity activity) {
        if (mEraseSdCard) {
            Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
            intent.putExtra(EXTRA_REASON, "WipeAllFlash");
            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
            activity.startService(intent);
        } else {
            Intent intent = new Intent(ACTION_MASTER_CLEAR);
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra(EXTRA_REASON, "MasterClearConfirm");
            activity.sendBroadcast(intent);
            // Intent handling is asynchronous -- assume it will happen soon.
        }
    }

    /**
     * Configure the UI for the final confirmation interaction
     */
//    private void establishFinalConfirmationState() {
//        mContentView.findViewById(R.id.execute_master_clear)
//                .setOnClickListener(mFinalClickListener);
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        if (UserManager.get(getActivity()).hasUserRestriction(
//                UserManager.DISALLOW_FACTORY_RESET)) {
//            return inflater.inflate(R.layout.master_clear_disallowed_screen, null);
//        }
//        mContentView = inflater.inflate(R.layout.master_clear_confirm, null);
//        establishFinalConfirmationState();
//        return mContentView;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Bundle args = getArguments();
//        mEraseSdCard = args != null && args.getBoolean(MasterClear.ERASE_EXTERNAL_EXTRA);
//    }




    /**
     * Returns true if Monkey is running.
     */
    public static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }
}
