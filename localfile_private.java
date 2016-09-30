package com.example.share.localfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SQLUse.DB_LoginAccount;
import com.example.SQLUse.DB_file;
import com.example.share.R;
import com.example.share.socket.ConstantValue;
import com.example.share.socket.GetFileInfo;
import com.example.share.socket.NetworkDetector;
import com.example.share.socket.ServerResponse;
import com.example.share.socket.SocketToServer;
import com.example.share.socket.UserRequest;
import com.example.share.socket.item_info;

public class localfile_private extends Activity implements OnClickListener, Callback {

	private Button back;
	private List<item_info> listitems;
	private ListView listview;
	private myAdapter adapter;

	//处理交互
	private Handler handler;
	//用户修改权限,通知服务器
	ProgressDialog pd;
	
	private int item_position;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localfile_private);

		back = (Button) findViewById(R.id.localfile_private_back);
		back.setOnClickListener(this);

		listview = (ListView) findViewById(R.id.localfile_private_listview);

		// 获取所有私有资源
		DB_file db = new DB_file(this);
		listitems = db.getAll(0);
		db.close();

		adapter = new myAdapter(this);
		listview.setAdapter(adapter);

		handler = new Handler(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0 == back)
			finish();
	}

	public final class HolderView {
		public TextView tv;
		public TextView play;
		public TextView delete;
		public TextView change;
	}

	public class myAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public myAdapter(Context context) {

			this.mInflater = LayoutInflater.from(context);

		}

		public int getCount() {

			return listitems.size();

		}

		public Object getItem(int position) {

			return listitems.get(position);

		}

		public long getItemId(int position) {

			return position;

		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			HolderView holder = null;
			if (convertView == null) {

				holder = new HolderView();

				// 可以理解为从vlist获取view 之后把view返回给ListView

				convertView = mInflater.inflate(
						R.layout.localfile_private_items, null);

				holder.tv = (TextView) convertView
						.findViewById(R.id.localfile_private_item_text);
				holder.play = (TextView) convertView
						.findViewById(R.id.localfile_private_item_play);
				holder.delete = (TextView) convertView
						.findViewById(R.id.localfile_private_item_delete);
				holder.change = (TextView) convertView
						.findViewById(R.id.localfile_private_item_change);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}

			holder.tv.setText(listitems.get(position).S_filename);

			// 给Button添加单击事件 添加Button之后ListView将失去焦点 需要的直接把Button的焦点去掉
			holder.play.setTag(position);
			holder.play.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String videoPath = listitems.get(position).S_path;
					
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					String strend = videoPath.substring(videoPath.lastIndexOf(".")+1);
					if(strend.equals("apk"))
						intent.setDataAndType(Uri.fromFile(new File(videoPath)) ,"application/vnd.android.package-archive");
					else
					{
						String mineType = null;
						if(strend.equals("mp4"))
							mineType = "video/mp4";
						else if(strend.equals("rmvb"))
							mineType = "video/vnd.rn-realvideo";
						else if(strend.equals("mkv"))
							mineType = "video/x-matroska";
						else if(strend.equals("3gp"))
							mineType = "video/3gpp";
						else if(strend.equals("rm"))
							mineType = "application/vnd.rn-realmedia";
						else if(strend.equals("flv"))
							mineType = "video/x-flv";
						else if(strend.equals("avi"))
							mineType = "video/x-msvideo";
						else if(strend.equals("txt"))
							mineType = "text/plain";
						else if(strend.equals("doc"))
							mineType = "application/msword";
						else if(strend.equals("pdf"))
							mineType = "application/pdf";
						intent.setDataAndType(Uri.parse(videoPath), mineType);
						}
					
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						// TODO: handle exception
						handler.sendEmptyMessage(3);
						return;
					}
					
				}
			});

			holder.delete.setTag(position);
			holder.delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					DB_file db_file = new DB_file(localfile_private.this);
					db_file.delete(listitems.get(position).S_filename);
					db_file.close();

					File file = new File(listitems.get(position).S_path);
					if (file.exists()) {
						
						file.delete();
						listitems.remove(position);
						adapter.notifyDataSetChanged();
					}

				}
			});

			holder.change.setTag(position);
			holder.change.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!NetworkDetector.detect(getApplicationContext())) {
						Toast.makeText(getApplicationContext(), "网络有问题",
								Toast.LENGTH_LONG).show();
						return;
					}

					
					handler.sendEmptyMessage(0);
					new Thread(new Runnable(){
						public void run(){
							UserRequest request = new UserRequest();
							request.mark = "file_share";

							item_info item = listitems.get(position);
							
							DB_LoginAccount loginaccount = new DB_LoginAccount(
									localfile_private.this);
							item.S_username = loginaccount.getAccount();
							loginaccount.close();
							
							request.username = item.S_username;
							
							GetFileInfo getfileinfo = new GetFileInfo(
									item.S_path,localfile_private.this);
							item.S_md5 = getfileinfo.getMd5ByFile();
							item.I_size = getfileinfo.getSize();
							item.B_picture = getfileinfo.getThumbnail();
							item.S_path = ":8080"+item.S_path;
							item.I_tag = 0;
							request.userresource = new ArrayList<item_info>();
							request.userresource.add(item);

							SocketToServer socketoserver = new SocketToServer(
									ConstantValue.ServerIp, ConstantValue.ServerPort);
							ServerResponse response = socketoserver
									.SendUserRequest(request);

							if(response == null){
								handler.sendEmptyMessage(1);
								return;
							}
							if(response.mark == null){
								handler.sendEmptyMessage(1);
								return;
							}
							if (response.mark.equals("success_share")) {
								item_position=position;
								handler.sendEmptyMessage(2);					
							}

						}
						
					}).start();
					
				}
			});
			return convertView;
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case 0:
			pd = new ProgressDialog(this);
			pd.setTitle("共享");
			pd.setMessage("正在做种");
			pd.setIcon(getResources().getDrawable(R.drawable.apk));
			pd.show();break;
			
		case 1:
			Toast.makeText(this, "服务器繁忙!", Toast.LENGTH_SHORT).show();
			pd.cancel();break;
		case 2:
			Toast.makeText(this, "共享成功!", Toast.LENGTH_SHORT).show();
			DB_file db_file = new DB_file(localfile_private.this);
			db_file.change(listitems.get(item_position).S_filename, 1);
			db_file.close();

			listitems.remove(item_position);
			adapter.notifyDataSetChanged();
			pd.cancel();break;
		case 3:
			Toast.makeText(this, "未找到相应软件打开,请安装后打开!", Toast.LENGTH_SHORT).show();
			break;
			
		}
		return false;
	}
}
