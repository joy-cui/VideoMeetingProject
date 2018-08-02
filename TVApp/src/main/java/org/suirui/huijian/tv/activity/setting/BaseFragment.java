package org.suirui.huijian.tv.activity.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;

/**
 * Created by hh on 2018/4/17.
 */

public class BaseFragment extends Fragment{
    private static final SRLog log = new SRLog(BaseFragment.class.getName(), TVAppConfigure.LOG_LEVE);
    private onMyFragmentListener mListener;
    private Fragment mCurFragment;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTitle("");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        log.E("onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        log.E("onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        log.E("onPause()");
        changeTitle("");
    }

    @Override
    public void onStop() {
        super.onStop();
        log.E("onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.E("onDestroy()");
    }
    @Override
    public void onAttach(Context context) {
        try{
            mListener = (onMyFragmentListener) context;
        }catch (Exception e){
            throw new ClassCastException(context.toString()
                    + " must implement onChangeTitleListener");
        }
        super.onAttach(context);
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try{
                mListener = (onMyFragmentListener) activity;
            }catch (Exception e){
                throw new ClassCastException(activity.toString()
                        + " must implement onChangeTitleListener");
            }
//        }

    }

    public void switchFragment(Fragment fragment){
        FragmentTransaction mFtransaction = getFragmentManager().beginTransaction();
        if(fragment != null){
            if (!fragment.isAdded()){
                log.E("switchFragment():add");
                mFtransaction.add(R.id.main_content, fragment);
            }else{
                log.E("switchFragment():show");
                mFtransaction.show(fragment);
            }
            mFtransaction.hide(this);
            mFtransaction.addToBackStack(null);
            mCurFragment = fragment;
            mFtransaction.commit();
        }
    }

    public void switchFragment(Fragment showFragment,Fragment hideFragment){
        FragmentTransaction mFtransaction = getFragmentManager().beginTransaction();
        if(showFragment != null){
            if (!showFragment.isAdded()){
                mFtransaction.add(R.id.main_content, showFragment);
            }else{
                log.E("switchFragment():show");
                mFtransaction.show(showFragment);
            }
            mFtransaction.hide(hideFragment);
            mFtransaction.addToBackStack(null);
            mCurFragment = showFragment;
            mFtransaction.commit();
        }
    }

    public Fragment getCurFragment(){
        return mCurFragment;
    }

    public void changeTitle(String title){
        mListener.onChangeTitle(title);
    }

    public void onBack() {
        if (!this.getFragmentManager().popBackStackImmediate()) {
            getActivity().supportFinishAfterTransition();
        }
    }

    public interface onMyFragmentListener{
        void onChangeTitle(String title);
    }

}
