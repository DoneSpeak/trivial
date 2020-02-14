package donespeak.com.netsourcedownload;

import android.content.Context;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import android.os.Handler;
import android.util.Log;

import donespeak.com.netsourcedownload.Helper;
import donespeak.com.netsourcedownload.RunReturnType;


public class DownloadRunnable implements Runnable{
    public String urlStr;
    public String saveDirStr;
    public Handler handler = null;

    DownloadRunnable(Handler handler,String urlStr, String savaDirStr){
        this.urlStr = urlStr;
        this.saveDirStr = savaDirStr;
        this.handler = handler;
    }

    @Override
    public void run(){

        Log.i("runnable", "进入了");
        Message message = new Message();

        message = new Message();
        message.what = RunReturnType.STARTDOWNLOAD;
        handler.sendMessage(message);

        if(!Helper.hasSDcard()){
//            mutiDialog.cteateWarningDialog("sd被移除，无法下载资源", "我知道了");
            Log.i("runnable","没有sd卡");
            message = new Message();
            message.what = RunReturnType.SDNotExit;
            handler.sendMessage(message);
            return;
        }

        //获得文件名和文件的content-type
        String contentType = null;
        String fileNameWithContentType = Helper.getFileNameWithContentTypeFromUrl(urlStr);
        if(fileNameWithContentType == null){
            Log.i("runnable","不！我怎么啦！");
            message = new Message();
            message.what = RunReturnType.FAILTOGETNAME;
            handler.sendMessage(message);
            return;
        }
        contentType = fileNameWithContentType.substring(0,fileNameWithContentType.indexOf("#"));
        String fileName = fileNameWithContentType.substring(fileNameWithContentType.indexOf("#") + 1);

        Log.i("runnable","名字" + fileName);
        Log.i("runnable", "内容" + contentType);


        FileOutputStream fos =  null;
        HttpURLConnection conn = null;
        DataInputStream	dis = null;
        try{
            URL url = new URL(urlStr);
            //使用编码下载，防止出现乱码
            String encode = Helper.getFileEncoding(url);

            try{
                conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000);// 设置连接超时
                conn.setReadTimeout(30 * 1000);
                //缓冲输入以提高效率
                dis = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            }catch(Exception e){
                //当网页设置权限限制爬虫抓取网页，设置服务代理，绕过限制

                Log.i("runnable", "需要服务代理");
                conn = (HttpURLConnection)new URL(urlStr).openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000);// 设置连接超时
                conn.setReadTimeout(30 * 1000);
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                //缓冲输入以提过小路
                dis = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            }

            fos = new FileOutputStream(saveDirStr + File.separator + fileName); // 输出
            Log.i("runnable", saveDirStr + File.separator + fileName);
            int data = -1;
            byte b[] = new byte[1024];
            while ((data = dis.read(b,0,1024)) != -1) {
                fos.write(b, 0, data);
                if(MainActivity.isStop == true){
                    MainActivity.isStop = false;
                    fos.flush();
                    File file = new File(saveDirStr + File.separator + fileName);
                    if(file.exists()){
                        file.delete();
                    }
                    message = new Message();
                    message.what = RunReturnType.STOPDOWNLOAD;
                    handler.sendMessage(message);
                    return;
                }
            }
            fos.flush();
            Log.i("runnable", "下载完成");
            String fileInfo = contentType + "#" + saveDirStr + File.separator + fileName;
            message = new Message();
            message.what = RunReturnType.FINISHDOWNLOAD;
            message.obj = fileInfo;
            handler.sendMessage(message);
        }catch(Exception e){
            //TODO 对话框改为通知栏提示框
            Log.i("runnable", "出错了");
            message = new Message();
            message.what = RunReturnType.DOWNLOADFAIL;
            handler.sendMessage(message);
//            mutiDialog.cteateWarningDialog(urlStr + "\n" + "下载失败", "我知道了");
            e.printStackTrace();
            return;
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

    }
}
