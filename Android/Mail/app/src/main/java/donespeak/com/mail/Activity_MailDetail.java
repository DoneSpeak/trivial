package donespeak.com.mail;

import donespeak.com.mail.bean.MailBean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeUtility;

import donespeak.com.mail.someClass.MailSender;

public class Activity_MailDetail extends AppCompatActivity implements View.OnClickListener {

    public static ProgressDialog pd;
    private String boxType;
    private MailBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_detail);

        Intent intent = this.getIntent();
        bean = (MailBean)intent.getSerializableExtra("email");
        boxType = (String)intent.getSerializableExtra("boxType");

        //为按钮添加点击事件监听器
        ImageView backbtn = (ImageView)findViewById(R.id.detail_back);
        ImageButton delbtn = (ImageButton)findViewById(R.id.bbar_delete);
        ImageButton flagbtn = (ImageButton)findViewById(R.id.bbar_flag);
        ImageButton repbtn = (ImageButton)findViewById(R.id.bbar_reply);
        ImageButton transbtn = (ImageButton)findViewById(R.id.bbar_transmit);

        //消息头
        TextView title = (TextView)findViewById(R.id.mail_detail_title);
        title.setText(bean.getTitle());
        TextView sender = (TextView)findViewById(R.id.mail_detail_sender);
        String senderStr = bean.getFromAddress() + "<" + bean.getFromWho() + ">";
        sender.setText(senderStr);
        TextView time = (TextView)findViewById(R.id.mail_detail_time);
        time.setText(bean.getTime());


        WebView content = (WebView)findViewById(R.id.mail_detail_content);
        WebSettings settings = content.getSettings();
        settings.setSupportMultipleWindows(false);
        settings.setSupportZoom(true);

        pd = ProgressDialog.show(this, "请稍候...", "正在加载", true, true);

        ShowMailDetail showDetailTask = new ShowMailDetail(MainActivity.userEmail,MainActivity.userPassword,boxType,bean.getMessageUID());
        showDetailTask.execute();
//        //附件按钮
//        Button attachmentBtn = (Button)findViewById(R.id.mail_detail_btn);
//        if(bean.isHasAttachment()) {
//            attachmentBtn.setAlpha(1);
//            attachmentBtn.setEnabled(true);
//        }
//        attachmentBtn.setOnClickListener(this);


        backbtn.setOnClickListener(this);
        delbtn.setOnClickListener(this);
        flagbtn.setOnClickListener(this);
        repbtn.setOnClickListener(this);
        transbtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int item = v.getId();
        Log.i("touch","点击了");
        switch (item){
            case R.id.detail_back:{
                //注销当前活动，返回上一页面
                Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
                finish();
                break;
            }
            case R.id.bbar_delete:{
                //TODO 删除邮件
                Log.i("touch", "点击删除");
                if(true){
                    return;
                }
                pd = ProgressDialog.show(this, "请稍候...", "正在删除", true, true);
                AsyncTask deleteTask =  new AsyncTask<Void,Void,Integer>() {
                    @Override
                    protected Integer doInBackground(Void... args) {
                        try{
                            DeleteMailByUID(bean.getMessageUID());
                        }catch(Exception e){
                            return 1; //失败
                        }

                        return 0; //成功
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        pd.dismiss();
                        if(integer == 1){
                            Toast.makeText(Activity_MailDetail.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }else{
                            finish();
                        }

                    }

                };

                break;
            }
            case R.id.bbar_flag:{
                //TODO 标记邮件 弹出菜单 标记为未读或者红旗邮件
                Toast.makeText(this,"弹出菜单",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bbar_reply:{
                //TODO 回复
                Toast.makeText(this,"回复",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bbar_transmit:{
                //TODO 转发
                Toast.makeText(this,"转发",Toast.LENGTH_SHORT).show();
                break;
            }case R.id.mail_detail_btn:{
                //TODO 打开一个线程下载文件
                pd = ProgressDialog.show(this, "请稍候...", "正在下载", false, false);
                Toast.makeText(this,"开始下载",Toast.LENGTH_LONG).show();
                downloadAttachmentTask task = new downloadAttachmentTask(MainActivity.userEmail,MainActivity.userPassword,boxType,bean.getMessageUID());
                task.setActivity(this);
                task.execute();
                break;
            }
        }
    }

    public void DeleteMailByUID(long uid) throws MessagingException{
        Properties prop = MailSender.getProperties("imap." + MainActivity.userEmail.substring(MainActivity.userEmail.lastIndexOf("@") + 1));
        Session session = Session.getInstance(prop);
        IMAPStore store = null;
        IMAPFolder folder = null;

        try {
            store = (IMAPStore) session.getStore("imaps");
            store.connect(MainActivity.userEmail, MainActivity.userPassword);
            //TODO 改为IMAPFOLDER
            folder = (IMAPFolder)store.getFolder(boxType);
            Log.i("mailDetail","deletegetFolder");
            folder.open(Folder.READ_WRITE);
            //找不到相应的folder
            if (folder == null) {
                throw new Exception("");
            }
            Log.i("mailDetail", "delete Folder.READ_WRITE)");
            Message message = folder.getMessageByUID(uid);
            Log.i("mailDetail","getMessageByUID");
            if(message == null){
                throw new Exception("");
            }
            //删除邮件操作
            message.setFlag(Flags.Flag.DELETED, true);
            folder.close(true);
            Log.i("mailDetail", "Flags.Flag.DELETED");

        }catch(MessagingException e){
            if(MainActivity.DEBUG){
                e.printStackTrace();
                Log.i("mailDetail", e.toString());
            }

            throw e;

        }finally{
            if(store != null && store.isConnected()){
                try {
                    store.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(folder != null && folder.isOpen()){
                try {
                    folder.close(false);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return;

        }
    }


    class ShowMailDetail extends AsyncTask<Void,Void,String> {

        StringBuffer bodytext = null;
        boolean attachmentflag = false;
        String boxType = null;
        String userEmail = null;
        String password = null;
        long msgUID;

        public ShowMailDetail(String userEmail, String password, String boxType, long msgUID){
            this.userEmail = userEmail;
            this.password = password;
            this.boxType = boxType;
            this.msgUID = msgUID;
        }

        @Override
        protected String doInBackground(Void...args0){

            return getBodyText(msgUID);
        }

        @Override
        protected void onPostExecute(String result) {
            //附件按钮
            Button attachmentBtn = (Button)findViewById(R.id.mail_detail_btn);
            if(hasAttachment(msgUID)) {
                attachmentBtn.setAlpha(1);
                attachmentBtn.setEnabled(true);
            }
            attachmentBtn.setOnClickListener(Activity_MailDetail.this);

            WebView content = (WebView)findViewById(R.id.mail_detail_content);
            content.loadDataWithBaseURL(null, result, "text/html", "utf-8", null);

            pd.dismiss();
        }

        public String getBodyText(long uid){
            if(bodytext == null){
                getMailBodyTextByUID(uid);
            }
            return bodytext.toString();
        }

        public boolean hasAttachment(long uid){
            if(bodytext == null){
                getMailBodyTextByUID(uid);
            }
            return attachmentflag;
        }

        /**
         * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
         */
        public void getMailContent(Part part) throws Exception {
            String contenttype = part.getContentType();
            //判断是否为附件，附件含有名称，正文不可以读取附件
            int nameindex = contenttype.indexOf("name");
            boolean conname = false;
            if (nameindex != -1)
                conname = true;
            if (MainActivity.DEBUG) {
                System.out.println("CONTENTTYPE: " + contenttype);
            }

            if (part.isMimeType("text/plain") && !conname) {
                bodytext.append(part.getContent().toString());
            } else if (part.isMimeType("text/html") && !conname) {
                bodytext.append(part.getContent().toString());
            } else if (part.isMimeType("multipart/*")) {
                //多重结构，需要递归解析
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    BodyPart mpart = multipart.getBodyPart(i);
                    if(!attachmentflag) {
                        String disposition = mpart.getDisposition();
                        if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
                            attachmentflag = true;
                        } else {
                            String contype = mpart.getContentType();
                            if (contype.toLowerCase().indexOf("application") != -1)
                                attachmentflag = true;
                            if (contype.toLowerCase().indexOf("name") != -1)
                                attachmentflag = true;
                        }
                    }
                    getMailContent(multipart.getBodyPart(i));
                }
            } else if (part.isMimeType("message/rfc822")) {
                getMailContent((Part) part.getContent());
            }
        }

        public void getMailBodyTextByUID(long uid){
            Properties prop = MailSender.getProperties("imap." + userEmail.substring(userEmail.lastIndexOf("@") + 1));
            Session session = Session.getInstance(prop);
            IMAPStore store = null;
            IMAPFolder folder = null;
            bodytext = new StringBuffer("");
            try {
                store = (IMAPStore) session.getStore("imaps");
                store.connect(userEmail, password);
                //TODO 改为IMAPFOLDER
                folder = (IMAPFolder)store.getFolder(boxType);
                Log.i("mailDetail","getFolder");
                folder.open(Folder.READ_WRITE);
                //找不到相应的folder
                if (folder == null) {
                    return;
                }
                Message message = folder.getMessageByUID(uid);
                Log.i("mailDetail","getMessageByUID");
                if(message == null){
                    return;
                }
                //解析bodyText
                getMailContent(message);
                Log.i("mailDetail", "getMailContent");

            }catch(MessagingException e){
                if(MainActivity.DEBUG){
                    e.printStackTrace();
                    Log.i("mailDetail", e.toString());
                }

            }finally{
                if(store != null && store.isConnected()){
                    try {
                        store.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if(folder != null && folder.isOpen()){
                    try {
                        folder.close(false);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                return;

            }
        }
    }


}


//下载附件
class downloadAttachmentTask extends AsyncTask<Void,Void,Integer> {

    public static final Integer SAVESUCCESS = 0 ;
    public static final Integer SAVEFAIL = 1 ;

    StringBuffer bodytext = null;
    boolean attachmentflag = false;
    String boxType = null;
    String userEmail = null;
    String password = null;
    Activity activity;
    long msgUID;

    private String saveAttachPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GRMail";

    public downloadAttachmentTask(String userEmail,String password,String boxType,long msgUID){
        this.userEmail = userEmail;
        this.password = password;
        this.boxType = boxType;
        this.msgUID = msgUID;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(Void...args0){

        File saveDir = new File(saveAttachPath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        try {
            getMailAttachByUID(msgUID);
            return SAVESUCCESS;
        }catch(Exception e){
            return SAVEFAIL;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        //保存完成
        if(result == SAVEFAIL){
            Activity_MailDetail.pd.dismiss();
            new AlertDialog.Builder(activity)
                    .setTitle("失败")
                    .setMessage("附件下载失败")
                    .setPositiveButton("确定", null)
                    .show();
        }else{
            Activity_MailDetail.pd.dismiss();
            //TODO 添加根据文件类型调用手机默认打开一个应用的方法
            new AlertDialog.Builder(activity)
                    .setTitle("成功")
                    .setMessage("附件下载成功" + "\r\n请查看目录" + saveAttachPath )
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    //保存文件
    private void saveFile(String fileName, InputStream in) throws Exception {

        File storefile = new File(saveAttachPath + File.separator + fileName);
        Log.i("saveFile",saveAttachPath + File.separator + fileName);
        storefile.createNewFile();

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(bos != null){
                bos.close();
            }
            if(bis != null){
                bis.close();
            }

        }
    }

    //递归获得附件并保存
    public void saveAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);//主体部分得到处理
                String disposition = mpart.getDisposition();
                if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)))) {//ATTACHMENT附件
                    fileName = mpart.getFileName();
                    if (fileName.toLowerCase().indexOf("gb18030") != -1) {
                        fileName = MimeUtility.decodeText(fileName);
                    }
                    saveFile(fileName, mpart.getInputStream());
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart);
                } else {
                    fileName = mpart.getFileName();
                    if ((fileName != null) && (fileName.toLowerCase().indexOf("GB18030") != -1)) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mpart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent());
        }
    }

    public void getMailAttachByUID(long uid) throws MessagingException{
        Properties prop = MailSender.getProperties("imap." + userEmail.substring(userEmail.lastIndexOf("@") + 1));
        Session session = Session.getInstance(prop);
        IMAPStore store = null;
        IMAPFolder folder = null;
        bodytext = new StringBuffer("");
        try {
            store = (IMAPStore) session.getStore("imaps");
            store.connect(userEmail, password);
            //TODO 改为IMAPFOLDER
            folder = (IMAPFolder)store.getFolder(boxType);
            Log.i("mailDetail","getFolder");
            folder.open(Folder.READ_WRITE);
            //找不到相应的folder
            if (folder == null) {
                return;
            }
            Message message = folder.getMessageByUID(uid);
            Log.i("mailDetail","getMessageByUID");
            if(message == null){
                return;
            }
            //调用保存附加操作函数
            saveAttachMent(message);
            Log.i("mailDetail", "getMailContent");

        }catch(MessagingException e){
            if(MainActivity.DEBUG){
                e.printStackTrace();
                Log.i("mailDetail", e.toString());
            }

            throw e;

        }finally{
            if(store != null && store.isConnected()){
                try {
                    store.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(folder != null && folder.isOpen()){
                try {
                    folder.close(false);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return;

        }
    }
}
