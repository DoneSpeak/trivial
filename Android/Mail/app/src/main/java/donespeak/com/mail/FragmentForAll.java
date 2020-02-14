package donespeak.com.mail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import donespeak.com.mail.bean.MailBean;
import donespeak.com.mail.someClass.MailSender;

/**
 * Created by glorior on 2016/6/18.
 */
public class FragmentForAll extends Fragment implements AdapterView.OnItemClickListener {

    private View layoutView;
    private ListView mailListView;
    public AdapterInbox adapter;
    public ProgressDialog pd;
    private List<MailBean> mailList = new ArrayList<MailBean>();

    private int layoutId;
    private String boxType;

    public void setFragmentForAll(int layoutId,String boxType){
        this.layoutId = layoutId;
        this.boxType = boxType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        adapter = new AdapterInbox(this.getActivity(),mailList);
        layoutView = inflater.inflate(layoutId, null);
        mailListView = (ListView)layoutView.findViewById(R.id.box_maillist);

        //初始化ListView，实现数据源
        mailListView.setAdapter(adapter);
        mailListView.setOnItemClickListener(this);
        //得到mail数据
        if((MainActivity.userEmail != null) && (MainActivity.userEmail.length()!= 0)) {
            //显示加载进度条，可被用户打断
            pd = ProgressDialog.show(getActivity(),"请稍候...","正在加载",true,true);
            new GetMailBean().execute((Void) null);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    ParseMail parseMail = new ParseMail(MainActivity.userEmail, MainActivity.userPassword);
//                    mailList = parseMail.getMailList();
//                    //销毁进度条
//                    adapter.notifyDataSetChanged();
//                    pd.dismiss();
//                }
//
//            }).start();
        }

        return layoutView;
    }

    @Override
    public void onPause() {
        super.onPause();
//        UIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    @Override
    public void onStart() {
        super.onStart();
//        UIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        UIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //启动MailDetail，其中的getActivity()方法是获取Fragment所在的Activity对象
        MailBean bean = mailList.get(position);
        Intent intent = new Intent(FragmentForAll.this.getActivity(), Activity_MailDetail.class);
        intent.putExtra("email",bean);
        intent.putExtra("boxType","Inbox");
        startActivity(intent);

    }

    //获取邮件数据线程
    public class GetMailBean extends AsyncTask<Void, Void, Integer> {

        public GetMailBean() {
            super();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            ParseMail parseMail = new ParseMail(MainActivity.userEmail, MainActivity.userPassword);
            parseMail.getMailList();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
            Log.i("message", "onPostExecute");
            adapter.notifyDataSetChanged();
            pd.dismiss();
            Log.i("message", " pd.dismiss();");

        }

        @Override
        protected void onCancelled() {
//            super.onCancelled();
            pd.dismiss();
        }
    }

    class ParseMail {

        private String dateformat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

        private javax.mail.Message message;
        private String userEmail;
        private String password;
        private boolean attachmentflag = false;
        private StringBuffer bodytext = new StringBuffer();

        public ParseMail(javax.mail.Message message) {
            this.message = message;
        }

        public ParseMail(String userEmail,String password) {
            this.userEmail = userEmail;
            this.password = password;
        }

        //获得发件人的名称
        public String getFromWho() throws Exception {
            InternetAddress address[] = (InternetAddress[]) message.getFrom();
            String who = address[0].getPersonal();
            if (who == null)
                who = "";
            return who;
        }

        //获得发件人的地址
        public String getFromAddress() throws Exception {
            InternetAddress address[] = (InternetAddress[]) message.getFrom();
            String addr = address[0].getAddress();
            if (addr == null)
                addr = "";
            return addr;
        }

        public boolean hasAttachment(){
            return attachmentflag;
        }

        /**
         * 获得邮件的收件人，抄送，和密送的地址和姓名
         * HashMap中email为key,personal为value
         */
        public HashMap<String, String> getMailAddressMap(int type) throws Exception {
            HashMap<String, String> mailAddrs = new HashMap<String, String>();
            InternetAddress[] address = null;

            switch (type) {
                case MainActivity.RECIPIENTTYPE_TO: {
                    address = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
                    break;
                }
                case MainActivity.RECIPIENTTYPE_CC: {
                    address = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
                    break;
                }
                case MainActivity.RECIPIENTTYPE_BCC: {
                    address = (InternetAddress[]) message.getRecipients(Message.RecipientType.BCC);
                    break;
                }
                default: {
                    throw new Exception("Error emailaddr type!");
                }
            }

            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String email = address[i].getAddress();
                    if (email == null)
                        email = "";
                    else {
                        email = MimeUtility.decodeText(email);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null)
                        personal = "";
                    else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    mailAddrs.put(email, personal);
                }

            }
            return mailAddrs;
        }

        /**
         * 获得邮件主题
         */
        public String getSubject() throws MessagingException {
            String subject = "";
            try {
                subject = MimeUtility.decodeText(message.getSubject());
                if (subject == null)
                    subject = "";
            } catch (UnsupportedEncodingException exce) {
                if (MainActivity.DEBUG) {
                    exce.printStackTrace();
                }
            }
            return subject;
        }

        /**
         * 获得邮件发送日期
         */
        public String getSentDate() throws Exception {
            Date sentdate = message.getSentDate();
            SimpleDateFormat format = new SimpleDateFormat(dateformat);
            return format.format(sentdate);
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

        /**
         * 获得邮件正文内容
         */
        public String getBodyText() throws Exception {

            return bodytext.toString();
        }

        /**
         * 【设置日期显示格式】
         */
        public void setDateFormat(String format) {
            this.dateformat = format;
        }


        public void getMailList(){
//           getMailByLink(0,userEmail,password,MainActivity.BOXTYPE_INBOX);
            getMailByLink(0,userEmail,password,"INBOX");
        }

        public void getMailByLink(int x, String emailNum, String password, String boxType) {
            Log.i("message", "getMailByLink");
//            List<MailBean> list = new ArrayList<MailBean>();
//            Properties prop = System.getProperties();
//            prop.put("mail.store.protocol", "imap");//这是邮件协议  ＩＭＡＰ
//            prop.put("mail.imap.host", "imap." + userEmail.substring(userEmail.lastIndexOf("@") + 1));  //收邮件主机名
            //TODO 使用getProperties会出问题
            Properties prop = MailSender.getProperties("imap." + userEmail.substring(userEmail.lastIndexOf("@") + 1));
            Session session = Session.getInstance(prop);
            IMAPStore store = null;
            Folder folder = null;
            try {
                store = (IMAPStore) session.getStore("imaps");
                store.connect(userEmail, password);
                //TODO 改为IMAPFOLDER
                folder = store.getFolder(boxType);

                Folder f  =  store.getDefaultFolder();
                Folder[] flist = f.list();
                for(int i = 0;i < flist.length; i++){
                    Log.i("folder",flist[i].getName());
                }
                //找不到相应的folder
                if (folder == null) {
                    return;
                }

                folder.open(Folder.READ_WRITE);

                if (MainActivity.DEBUG) {
                    int total = folder.getMessageCount();
                    Log.i("message", "mail account " + total);
                }
                javax.mail.Message[] messages = null;
                try {
                    messages = folder.getMessages();
                }catch(Exception e){
                    Log.i("message", "open");
                }

                //TODO 判断这个清楚之后是否会有什么不好的影响有的话可以创建一个全新的，然后在清空，之后赋值
                mailList.clear();//清空数据
                if (messages.length > 0) {
                    if (MainActivity.DEBUG) {
                        System.out.println("Messages's length: " + messages.length);
                    }
                    ParseMail pmm = null;
                    for (int i = 0;  x < messages.length; i++, x++) {
                        //从最后往前读取
                        pmm = new ParseMail((MimeMessage) messages[messages.length - 1 - x]);
                        try {

                            boolean isRead;
                            String read;
                            Flags flags = messages[messages.length - 1 - x].getFlags();
                            if (flags.contains(Flags.Flag.SEEN)) {

                                isRead = true;
                            } else {

                                isRead = false;
                            }
                            pmm.setDateFormat("yy年MM月dd日 HH:mm");

//                            pmm.getMailContent((Part) messages[messages.length - 1 - x]);
                            MailBean mailBean = new MailBean();
//                            map = new HashMap<String, Object>();
//                            map.put(Fragment_inbox.MAIL_ITEM_FROMWHO, pmm.getFromWho());
//                            map.put(Fragment_inbox.MAIL_ITEM_FROMADDR, pmm.getFromAddress());
//                            map.put(Fragment_inbox.MAIL_ITEM_TITLE, pmm.getSubject());
//                            map.put(Fragment_inbox.MAIL_ITEM_TIME, pmm.getSentDate());
//                            map.put(Fragment_inbox.MAIL_ITEM_BODYTEXT, pmm.getBodyText());
//                            //UID用户获取该message，从中获取附件信息
//                            map.put(Fragment_inbox.MAIL_ITEM_UID, folder.getUID(messages[messages.length - 1 - x]));
//                            map.put(Fragment_inbox.MAIL_ITEM_MSGNUM, messages[messages.length - 1 - x].getMessageNumber());

                            //点开之后在打开，节省流量
//                            mailBean.setBodytext(pmm.getBodyText());
//                            Log.i("message", "setBodytext");
                            mailBean.setFromWho(pmm.getFromWho());
                            Log.i("message", "setFromWho");
                            mailBean.setFromAddress(pmm.getFromAddress());
                            Log.i("message", "setFromAddress");
                            mailBean.setTitle(pmm.getSubject());
                            Log.i("message", "getSubject");
                            mailBean.setTime(pmm.getSentDate());
                            Log.i("message", "getSentDate");
                            //UID用户获取该message，从中获取附件信息
                            mailBean.setMessageUID(((IMAPFolder) folder).getUID(messages[messages.length - 1 - x]));
                            Log.i("message", "setMessageUID");
                            mailBean.setMsgNum(messages[messages.length - 1 - x].getMessageNumber());
                            //这一句必须在获取bodyText之后
//                            mailBean.setHasAttachment(pmm.hasAttachment());
//                            Log.i("message", "setMsgNum");
                            mailList.add(mailBean);
                            Log.i("message", "msgNum " + messages[messages.length - 1 - x].getMessageNumber());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    //数据获取结束
                    //将数据更新到ListView中

//                    return list;

                }
            } catch (javax.mail.NoSuchProviderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            } catch (MessagingException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
                return;
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
            }
//            return list;
        }

    }

}
