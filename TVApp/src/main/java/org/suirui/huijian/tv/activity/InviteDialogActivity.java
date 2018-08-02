package org.suirui.huijian.tv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.huijian.tv.prestener.impl.MeetingPrestener;
import org.suirui.srpaas.entry.ConferenceInfo;

/**
 * Created by cui on 2018/4/23.
 */

public class InviteDialogActivity extends AppCompatActivity {
SRLog log=new SRLog(InviteDialogActivity.class.getName(), TVAppConfigure.LOG_LEVE);
private String confId="";
private String confSubject;
private String content="";
private String confName="";
private String confPwd="";
private TextView invite_to_content_txt,invite_accept_txt,invite_refuse_txt;
ImageView invite_load_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_dialog_layout);
        initView();
        Intent intent=getIntent();
        setData(intent);


    }
    private void initView(){

        invite_to_content_txt=(TextView) getDelegate().findViewById(R.id.invite_to_content_txt);
        invite_accept_txt=(TextView) getDelegate().findViewById(R.id.invite_accept_txt);
        invite_refuse_txt=(TextView) getDelegate().findViewById(R.id.invite_refuse_txt);
        invite_load_img=(ImageView)getDelegate().findViewById(R.id.invite_load_img);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.invite_loading_animation);
        invite_load_img.startAnimation(animation);//开始动画

        invite_accept_txt.setFocusable(true);

    }
    private void setData(Intent intent){
        if(intent!=null){
            confId=intent.getStringExtra(TVAppConfigure.Invite.conf_id);
            confName=intent.getStringExtra(TVAppConfigure.Invite.conf_name);
            confSubject=intent.getStringExtra(TVAppConfigure.Invite.conf_subject);
            content=intent.getStringExtra(TVAppConfigure.Invite.invite_content);
            confPwd=intent.getStringExtra(TVAppConfigure.Invite.conf_pwd);
            if(StringUtil.isEmpty(confPwd)){
                confPwd="";
            }
            log.E("InviteDialogActivity..."+confId+" : "+confSubject+" : "+content+" : "+confPwd);
            if(!StringUtil.isEmpty(content)){
                invite_to_content_txt.setText(content);
            }

        }
        invite_accept_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加入会议
                new MeetingPrestener().joinMeeting(InviteDialogActivity.this, confSubject, confPwd);
                InviteDialogActivity.this.finish();
            }
        });
        invite_refuse_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              InviteDialogActivity.this.finish();
            }
        });
    }
}
