package donespeak.com.netsourcedownload;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class MainActivity extends AppCompatActivity {
    private Button btn_sure = null;
    private EditText edt_url = null;
    private String saveDirStr = null;
    public static String filePath = null;
    public static boolean isStop = false;

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RunReturnType.SDNotExit: {
                    mutiDialog.cteateWarningDialog(MainActivity.this, "sd被移除，无法下载资源", "我知道了");
                    btn_sure.setEnabled(true);
                    break;
                }
                case RunReturnType.DOWNLOADFAIL: {
                    mutiDialog.cteateWarningDialog(MainActivity.this, "下载失败", "我知道了");
                    btn_sure.setEnabled(true);
                    break;
                }
                case RunReturnType.FINISHDOWNLOAD: {

                    String fileInfo = (String)msg.obj;

                    filePath = fileInfo.substring(fileInfo.indexOf("#") + 1);
                    final String contentType = fileInfo.substring(0,fileInfo.indexOf("#"));

//                    mutiDialog.cteateWarningDialog(MainActivity.this, "0:" + fileName + "\n1:" + contentType, "我知道了");
//                    Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
//                    callMediaScanner(fileName); //TODO
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.tip)
                            .setIcon(R.drawable.wrenw)
                            .setMessage("下载完成")
                            .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = null;
                                    try {
                                        intent = Helper.getIntentToOpenFile(filePath, contentType);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                        intent = null;
                                    }
                                    if(intent == null){
                                        mutiDialog.cteateWarningDialog(MainActivity.this, "此文件无法打开！", "我知道了");
                                        btn_sure.setEnabled(true);
                                        return;
                                    }
                                    try {
                                        startActivity(intent);
                                    }catch (Exception e){
                                        Log.i("runnable","打不开呀！");
                                        mutiDialog.cteateWarningDialog(MainActivity.this, "此文件无法打开！", "我知道了");
                                        return;
                                    }
                                }
                            })
                            .setNeutralButton("打开路径", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which){

                                }
                            })
                            .create().show();
                    btn_sure.setEnabled(true);
                    break;
                }
                case RunReturnType.STARTDOWNLOAD: {
//                  mutiDialog.cteateWarningDialog(MainActivity.this, "开始下载", "我知道了");
                    Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
                    break;
                }
                case RunReturnType.FAILTOGETNAME:{
                    mutiDialog.cteateWarningDialog(MainActivity.this, "下载失败", "我知道了");
                    btn_sure.setEnabled(true);
                }
                case RunReturnType.STOPDOWNLOAD:{
                    Toast.makeText(MainActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    btn_sure.setEnabled(true);
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();
        startFunction();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        MainActivity.isStop = true;
    }


    //创建程序文件夹
    //存在sd卡就创建在sd卡中，否则创建在手机内存中
    public void createDirectory(){
        //TODO 创建应用程序文件夹

        String status = Environment.getExternalStorageState();
        //有sd卡，在sd卡里创建
        if(status.equals(Environment.MEDIA_MOUNTED)){
            //sd卡可以写入
            //TODO 这里实现的其实是存在手机的内部，并不是自己想的那样存到sd卡中
            saveDirStr = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Catcher";

        }else{
            //没法创建文件夹程序无法保存文件
            new AlertDialog.Builder(this)
                    //TODO R.string 和 @string/ 的区别
                    .setTitle(R.string.notDownload)
                    .setIcon(R.drawable.warning)
                    .setMessage("sd卡权限不足或者没有插入sd卡！")
                    .setPositiveButton("我知道了", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Do Nothing
                        }
                    }).create().show();
            return;
        }

        //每次保存文件的时候要注意判断saveDir是否还存在，因为在程序运行过程中，用户可能删除了该目录
        File saveDir =  new File(saveDirStr);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        //TODO 将saveDirStr存入配置文件，找到如何得到外置的sd的路径之后可以优化
//        Toast.makeText(MainActivity.this, saveDirStr, Toast.LENGTH_LONG).show();
    }

    public void startFunction(){
        btn_sure = (Button)findViewById(R.id.btn_sure);
        edt_url = (EditText) findViewById(R.id.edt_url);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得urlStr，判断是否合法
                btn_sure.setEnabled(false);
                String urlStr = edt_url.getText().toString();
                //补充url字符串
                if(urlStr.indexOf("://") < 0){
                    urlStr = "http://" + urlStr;
                }
                int httpError = Helper.checkHTTP(urlStr);
                if(urlStr.length() == 0 || urlStr == null){
                    mutiDialog.cteateTipDialog(MainActivity.this,"网址为空"+"\n" + "请输入资源下载网址" + "\n" + "请重新输入","我知道了");
                    return;
                }else if(httpError == HttpError.FORMATERROR){
                    mutiDialog.cteateTipDialog(MainActivity.this,"网址格式有误"+"\n"+"请重新输入","好的");
                    return;

                }else if(httpError == HttpError.NOTHTTP){
                    mutiDialog.cteateTipDialog(MainActivity.this,"输入网址不是http协议" + "\n" + "请输入http协议的网址", "好的");
                    return;

                }else if(httpError == HttpError.CORRECT){
                    //用线程在后台下载资源，同时弹出下载网址资源
                    new Thread(new DownloadRunnable(handler,urlStr,saveDirStr)).start();

                }else {
                    mutiDialog.cteateWarningDialog(MainActivity.this,"程序出错" + "\n" + "请关闭后重新启动", "我的天哪");
                    return;
                }
            }
        });


    }

}
