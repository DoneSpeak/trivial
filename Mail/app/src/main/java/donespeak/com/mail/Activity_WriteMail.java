package donespeak.com.mail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;

import donespeak.com.mail.bean.MailBean;
import donespeak.com.mail.someClass.MailSender;

public class Activity_WriteMail extends AppCompatActivity implements View.OnClickListener {
    public static MailSender sender = null;

    public static final int SEND_FAIL = 0;
    public static final int SEND_SUCCESS = 1;

    public ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);

        ImageView backbtn = (ImageView)findViewById(R.id.back);
        ImageView sendbtn = (ImageView)findViewById(R.id.action_send);

        backbtn.setOnClickListener(this);
        sendbtn.setOnClickListener(this);

        Intent intent = this.getIntent();
        final MailBean bean = (MailBean)intent.getSerializableExtra("email");

        if(bean != null){

            pd = ProgressDialog.show(this, "请稍候...", "正在加载", true, true);

            final String boxType = (String)intent.getSerializableExtra("boxType");

            showMailDetail showDetailTask = new showMailDetail(MainActivity.userEmail,MainActivity.userPassword,boxType,bean.getMessageUID());
            showDetailTask.execute();


            EditText subject = (EditText)findViewById(R.id.mail_write_title);
            EditText to = (EditText)findViewById(R.id.mail_write_to);

            subject.setText(bean.getTime());
            to.setText(bean.getFromAddress());


        }





    }

    @Override
    public void onClick(View v) {
        int item = v.getId();
        if(item== R.id.back) {
            new AlertDialog.Builder(this).setTitle("是否保存为草稿？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("保存草稿", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 判断收件人是否为空，不为空询问是否将内容保存为草稿
                            finish();
                        }
                    })
                    .setNegativeButton("删除草稿", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 销毁当前activity
                            finish();
                        }
                    }).show();
        }else if(item == R.id.action_send){
            //TODO 发送邮件
            sender = new MailSender(MainActivity.userEmail,MainActivity.userPassword);

            EditText to = (EditText)findViewById(R.id.mail_write_to);
            sender.setTOReceiver(to.getText().toString().trim());

            EditText cc = (EditText)findViewById(R.id.mail_write_cc);
            sender.setCCReceiver(cc.getText().toString().trim());

            EditText bcc = (EditText)findViewById(R.id.mail_write_bcc);
            sender.setBCCReceiver(bcc.getText().toString().trim());

            EditText subject = (EditText)findViewById(R.id.mail_write_title);
            sender.setSubject(subject.getText().toString().trim());
            Log.i("sendMail","BCC " + bcc.getText().toString().trim());
            EditText content = (EditText)findViewById(R.id.mail_write_content);
            sender.setBodyText(content.getText().toString().trim());

            //TODO 获取附件的文件路径数组
            new sendMailTask(Activity_WriteMail.this).execute();

//            Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this,"你点了什么？>&<",Toast.LENGTH_SHORT).show();
        }
    }

    class sendMailTask extends AsyncTask<Void,Void,Integer>{

        private Activity theActivity = null;

        public sendMailTask(Activity activity) {
            theActivity = activity;
        }

        @Override
        protected Integer doInBackground(Void...args0){
            try{
                Activity_WriteMail.sender.sendMessage();
            }catch(Exception e){
                if(MainActivity.DEBUG){
                 e.printStackTrace();
                }
                return SEND_FAIL;
            }
            return SEND_SUCCESS;
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result.intValue()){
                case Activity_WriteMail.SEND_FAIL:{
//                    Toast.makeText(theActivity,"发送失败",Toast.LENGTH_LONG);
                    new AlertDialog.Builder(theActivity)
                            .setTitle("发送失败")
                            .setMessage("发送失败！请稍后再发！")
                            .setPositiveButton("确定", null)
                            .show();
                    Log.i("sendMail","SEND_FAIL");
                    break;
                }
                case Activity_WriteMail.SEND_SUCCESS:{
                    Toast.makeText(theActivity,"发送成功",Toast.LENGTH_LONG);
                    Log.i("sendMail", "SEND_SUCCESS");
                    break;
                }
            }

        }
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this).setTitle("是否保存为草稿？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("保存草稿", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 判断收件人是否为空，不为空询问是否将内容保存为草稿
                        finish();
                    }
                })
                .setNegativeButton("删除草稿", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 销毁当前activity
                        finish();
                    }
                }).show();
    }

    class showMailDetail extends AsyncTask<Void,Void,String> {

        StringBuffer bodytext = null;
        boolean attachmentflag = false;
        String boxType = null;
        String userEmail = null;
        String password = null;
        long msgUID;

        public showMailDetail(String userEmail,String password,String boxType,long msgUID){
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

            EditText content = (EditText)findViewById(R.id.mail_write_content);
            content.setText(result);

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
