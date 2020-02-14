package donespeak.com.mail.someClass;

import android.util.Log;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import donespeak.com.mail.MainActivity;

/**
 * Created by glorior on 2016/6/21.
 */
public class ParseMail {

    public static final int RECIPIENTTYPE_TO = 0;
    public static final int RECIPIENTTYPE_CC = 1;
    public static final int RECIPIENTTYPE_BCC = 2;

    public static final String BOXTYPE_INBOX = "INBOX";
    public static final String BOXTYPE_DELETE_ITEMS = "Deleted Items";
    public static final String BOXTYPE_JUNK_EMAIL = "Junk E-mail";
    public static final String BOXTYPE_DRAFTS = "Drafts";
    public static final String BOXTYPE_SENT_ITEMS = "Sent Items";

    private String dateformat = "yy-MM-dd HH:mm"; // 默认的日前显示格式

    private Message message;
    private StringBuffer bodytext = new StringBuffer();

    public ParseMail(Message message) {
        this.message = message;
    }

    //获得发件人的名称
    public String getFromWho() throws Exception {
        InternetAddress address[] = (InternetAddress[]) message.getFrom();
        String who = address[0].getAddress();
        if (who == null)
            who = "";
        return who;
    }

    //获得发件人的地址
    public String getFromAddress() throws Exception {
        InternetAddress address[] = (InternetAddress[]) message.getFrom();
        String personal = address[0].getPersonal();
        if (personal == null)
            personal = "";
        return personal;
    }

    /**
     * 获得邮件的收件人，抄送，和密送的地址和姓名
     * HashMap中email为key,personal为value
     */
    public HashMap<String, String> getMailAddressMap(int type) throws Exception {
        HashMap<String, String> mailAddrs = new HashMap<String, String>();
        InternetAddress[] address = null;

        switch (type) {
            case ParseMail.RECIPIENTTYPE_TO: {
                address = (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
                break;
            }
            case ParseMail.RECIPIENTTYPE_CC: {
                address = (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
                break;
            }
            case ParseMail.RECIPIENTTYPE_BCC: {
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

    public List<Map<String, Object>> getMailByLink(int x, String email, String password, String boxType) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");//这是邮件协议  ＩＭＡＰ
        prop.put("mail.imap.host", "imap." + email.substring(email.lastIndexOf("@") + 1));  //收邮件主机名　以１６３邮箱为例
        Session session = Session.getInstance(prop);
        IMAPStore store;
        try {
            store = (IMAPStore) session.getStore("imap");
            store.connect(email, password);
            IMAPFolder folder = (IMAPFolder) store.getFolder(boxType);
            //找不到相应的folder
            if (folder == null) {
                return null;
            }
            folder.open(Folder.READ_WRITE);
            if (MainActivity.DEBUG) {
                int total = folder.getMessageCount();
                Log.v("mail", "mail account " + total);
            }
            Message[] messages = folder.getMessages();
            if (messages.length > 0) {
                Map<String, Object> map;
                if (MainActivity.DEBUG) {
                    System.out.println("Messages's length: " + messages.length);
                }
                ParseMail pmm = null;
                for (int i = 0; i < 10 && x < messages.length; i++, x++) {
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

                        pmm.getMailContent((Part) messages[messages.length - 1 - x]);
                        map = new HashMap<String, Object>();
                        map.put("from", pmm.getFromWho());
                        map.put("fromaddress", pmm.getFromAddress());
                        map.put("title", pmm.getSubject());
                        map.put("time", pmm.getSentDate());
                        map.put("bodytext", pmm.getBodyText());
                        //UID用户获取该message，从中获取附件信息
                        map.put("messageUID", folder.getUID(messages[messages.length - 1 - x]));
                        map.put("msgNum", messages[messages.length - 1 - x].getMessageNumber());
                        list.add(map);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                return list;

            }
        } catch (javax.mail.NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (MessagingException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            return null;
        }
        return list;
    }

}
