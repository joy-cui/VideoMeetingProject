package org.suirui.huijian.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.video.util.ParamUtil;
import com.suirui.srpaas.video.util.StringUtil;

import org.suirui.huijian.tv.R;
import org.suirui.huijian.tv.TVAppConfigure;
import org.suirui.srpaas.entry.ConferenceInfo;
import org.suirui.srpaas.entry.MeetDetailInfo;

import java.util.List;


/**
 * 会议列表选择item弹框
 */
public class ShowMeetDetailDialog extends Dialog {
SRLog log=new SRLog(this.getClass().getName(), TVAppConfigure.LOG_LEVE);
	private Context mContext;
	private TextView  txt_conf_id,txt_conf_name,txt_start_time,txt_encrypt_type,txt_long_time,txt_conf_pwd
			,txt_conf_property,txt_conf_site,txt_conf_participant,txt_conf_notif_email,txt_conf_notif_oa,txt_conf_notif_sms;
	MeetDetailInfo conferenceInfo;

	public ShowMeetDetailDialog(Context context, MeetDetailInfo meetDetailInfo) {
		super(context, R.style.hj_tv_custom_dialog);
		this.mContext = context;
		this.conferenceInfo=meetDetailInfo;

	}
	public ShowMeetDetailDialog(Context context, int theme,MeetDetailInfo meetDetailInfo) {
		super(context, theme);
		this.mContext = context;
		this.conferenceInfo=meetDetailInfo;

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.init();
	}

	private void init() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.show_meet_detail_dialog_layout, null);
		setContentView(view);

		  txt_conf_id = (TextView) view.findViewById(R.id.txt_conf_id);
		  txt_conf_name = (TextView) view.findViewById(R.id.txt_conf_name);
		  txt_conf_pwd = (TextView) view.findViewById(R.id.txt_conf_pwd);
		  txt_start_time = (TextView) view.findViewById(R.id.txt_start_time);
		  txt_long_time = (TextView) view.findViewById(R.id.txt_long_time);
		  txt_encrypt_type=(TextView) view.findViewById(R.id.txt_encrypt_type);
		  txt_conf_property = (TextView) view.findViewById(R.id.txt_conf_property);
		  txt_conf_site = (TextView) view.findViewById(R.id.txt_conf_site);
		  txt_conf_participant = (TextView) view.findViewById(R.id.txt_conf_participant);
		  txt_conf_notif_email= (TextView) view.findViewById(R.id.txt_conf_notif_email);
		  txt_conf_notif_oa= (TextView) view.findViewById(R.id.txt_conf_notif_oa);
		  txt_conf_notif_sms= (TextView) view.findViewById(R.id.txt_conf_notif_sms);


		int screenW = ParamUtil.getScreenWidth(mContext);
		int screenH = ParamUtil.getScreenHeight(mContext);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		if (screenW < screenH) {//竖屏
			params.width = screenW - 50;
		} else {//横屏
			params.width = screenW / 2+100;
		}
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
		setData();
	}
	private void setData(){
		if(conferenceInfo!=null) {
			txt_conf_id.setText(setTextValue(conferenceInfo.getSubject()));
			txt_conf_name.setText(setTextValue(conferenceInfo.getConfName()));
			txt_start_time.setText(setTextValue(conferenceInfo.getStartTime()));
			txt_long_time.setText(permanentenableValue(conferenceInfo.getPermanentenable()));
			txt_encrypt_type.setText(encrypt(conferenceInfo.getEncryptalg()));
			txt_conf_pwd.setText(setPwdTextValue(conferenceInfo.getConfPwd()));
			txt_conf_property.setText(confPropertyAudio(conferenceInfo.getAudioenable()) + " ; " + confPropertyVideo(conferenceInfo.getCameraautoenable()));
//		txt_conf_notif.setText(Notif(conferenceInfo.getMsgtype()));
			Notif(conferenceInfo.getMsgtype());
			txt_conf_site.setText(getParticipantsRooms(conferenceInfo.getRooms()));
			txt_conf_participant.setText(getParticipantsRooms(conferenceInfo.getParticipates()));
		}else{
			log.E("getMeetDetail...null");
		}

	}
	private String setPwdTextValue(String value){
		if(StringUtil.isEmpty(value)){
			return "";
		}else{
			StringBuffer bufStr=new StringBuffer();
			for(int i=0;i<value.length();i++){
				bufStr.append("*");
			}
			return bufStr.toString();
		}

	}
	private String setTextValue(String value){
		if(StringUtil.isEmpty(value)){
			return "";
		}else{
			return value;
		}

	}
	private String permanentenableValue(String value){
		if(StringUtil.isEmpty(value)){
			return "";
		}else{
			if(value.equals("1")){
				value= mContext.getString(R.string.hj_long_meeting);
			}else{
				value= mContext.getString(R.string.hj_short_meeting);
			}

			return value;
		}
	}
	//[{'type': '无加密', 'value': '0'}, {'type': 'AES_128', 'value': '1'},{'type': 'SM4', 'value': '2'}];
	private  String encrypt(String value){

		if(StringUtil.isEmpty(value)){
			return "";
		}else{
			if(value.equals("0")){
				value= mContext.getString(R.string.hj_no_encrypt);
			}else if(value.equals("1")){
				value= mContext.getString(R.string.hj_encrypt_aes);
			}else{
				value=mContext.getString(R.string.hj_encyypt_sm);

			}

			return value;
		}
	}
	//        micautoenable: 0,//入会后自动静音 0否 1是 camaraautoenable: 1,//入会自动打开视频 0否 1是

	private String confPropertyAudio(String value){
		if(StringUtil.isEmpty(value)){
			return "";
		}else{
			if(value.equals("1")){
				value= mContext.getString(R.string.hj_auto_close_audio);
			}else{
				value= mContext.getString(R.string.hj_auto_open_audio);
			}

			return value;
		}
	}
	private String confPropertyVideo(String value){
		if(StringUtil.isEmpty(value)){
			return "";
		}else{
			if(value.equals("1")){
				value= mContext.getString(R.string.hj_auto_open_video);
			}else{
				value= mContext.getString(R.string.hj_auto_close_video);
			}

			return value;
		}
	}
	//会议通知方式
		private String Notif(String value){
			if(StringUtil.isEmpty(value)){
				return "";
			}else{



//				var mtype=selectMeetingInfo.msgtype.split(",");
//				meetInfoVm.sms=getNotifType(mtype,1);
//				meetInfoVm.email=getNotifType(mtype,2);
//				meetInfoVm.oa=getNotifType(mtype,3);
				String[] mttype=value.split(",");
				if(mttype!=null) {
					for (int i = 0; i < mttype.length; i++) {
						if(mttype[i].equals("1")){
							txt_conf_notif_email.setVisibility(View.VISIBLE);
						}else if(mttype[i].equals("2")){
							txt_conf_notif_sms.setVisibility(View.VISIBLE);
						}else if(mttype[i].equals("3")){
							txt_conf_notif_oa.setVisibility(View.VISIBLE);
						}

					}
				}


				return value;
			}
		}
	private String getParticipantsRooms(List<MeetDetailInfo.Participantes> rooms){

			boolean isFlag=false;
		if(rooms==null){
			return "";
		}
		StringBuffer stringBuffer=new StringBuffer();
		Log.e("","getParticipantsRooms..."+rooms.size());
		for(MeetDetailInfo.Participantes participantes: rooms){
			if(participantes!=null && participantes.getNickname()!=null){
				if(isFlag){
					stringBuffer.append("    ");
				}
				stringBuffer.append(participantes.getNickname());
				isFlag=true;
			}
		}


		return stringBuffer.toString();

	}
}
