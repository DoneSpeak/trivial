package donespeak.com.mail.someClass;

import android.util.Log;

import com.sun.mail.smtp.SMTPSendFailedException;

import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import donespeak.com.mail.MainActivity;

/**
 * Created by glorior on 2016/6/22.
 */
public class MailSender {

    private String userEmail = null;
    private String password = null;

//    public static MimeMessage message;

    private String TOReceiver = null;
    private String protocol = "smtp";
    private String CCReceiver = null; // 抄送
    private String BCCReceiver = null; //密送
    private String subject = "";
    private String bodyText = "";
    private String[] attachments = null;

    public MailSender(String userEmail,String password){
        this.userEmail = userEmail;
        this.password = password;
    }

    public MailSender(String from,String TOReceiver,String CCReceiver,String BCCReceiver){
        this.TOReceiver = TOReceiver;
        this.CCReceiver = CCReceiver;
        this.BCCReceiver = BCCReceiver;
    }

    public void setTOReceiver(String TOReceiver) {
        this.TOReceiver = TOReceiver;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setCCReceiver(String CCReceiver) {
        this.CCReceiver = CCReceiver;
    }

    public void setBCCReceiver(String BCCReceiver) {
        this.BCCReceiver = BCCReceiver;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }

    public void sendMessage()throws MessagingException{
        if(MainActivity.DEBUG){
            Log.i("sendMail", "createSession");
        }
        Session session = createSession();
        if(MainActivity.DEBUG){
            Log.i("sendMail", "createMessage");
        }
        MimeMessage message = createMessage(session);

        if(MainActivity.DEBUG){
            Log.i("sendMail",session + "");
            Log.i("sendMail", "transport");
        }

        //获得Transport对象，并连接邮件服务器发送邮件
        Transport transport = null;
        try {
            transport = session.getTransport();

            if (MainActivity.DEBUG) {
                Log.i("sendMail", "connect: " + userEmail.substring(userEmail.lastIndexOf("@") + 1));
            }
            transport.connect("smtp." + userEmail.substring(userEmail.lastIndexOf("@") + 1), userEmail, password);
            if (MainActivity.DEBUG) {
                Log.i("sendMail", "after connect");
            }
            if (TOReceiver != null && TOReceiver.length() != 0) {
                transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            }
            if (CCReceiver != null && CCReceiver.length() != 0) {
                transport.sendMessage(message, message.getRecipients(Message.RecipientType.CC));
            }
            if (MainActivity.DEBUG) {
                Log.i("sendMail", "sendMessage");
            }
            if (BCCReceiver != null && BCCReceiver.length() != 0) {
                transport.sendMessage(message, message.getRecipients(Message.RecipientType.BCC));
            }
            if (MainActivity.DEBUG) {
                Log.i("sendMail", "close");
            }
        }catch(MessagingException e){
            if(MainActivity.DEBUG){
                e.printStackTrace();
            }
            throw e;
        }finally {
            if(transport != null && transport.isConnected()) {
                transport.close();
            }
        }

    }

    public Session createSession(){
        Properties props = MailSender.sendProperties("smtp." + userEmail.substring(userEmail.lastIndexOf("@") + 1));
//        props.setProperty("mail.transport.protocol",protocol);
        //设置认证信息
//        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(props);
        session.setDebug(MainActivity.DEBUG);
        return session;
    }

    public MimeMessage createMessage(Session session)throws MessagingException{
        MimeMessage  message = new MimeMessage(session);

        message.setFrom(new InternetAddress(userEmail));
        //TODO 确认parse方法的分隔符
        //设置发件人
        if(TOReceiver != null && TOReceiver.length() != 0) {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TOReceiver));
        }
        if(CCReceiver != null && BCCReceiver.length() != 0) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CCReceiver));
        }
        if(BCCReceiver != null && BCCReceiver.length() != 0){
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(BCCReceiver));
        }
        //标题
        message.setSubject(subject);
        //内容
        MimeMultipart multipart = new MimeMultipart("related");
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(bodyText, "text/html;charset=utf-8");
        multipart.addBodyPart(htmlBodyPart);

        MimeBodyPart attachBodyPart = null;
        if(attachBodyPart != null) {
            for (int i = 0; i < attachments.length; i++) {
                attachBodyPart = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(attachments[i]);
                attachBodyPart.setDataHandler(new DataHandler(fds));
                multipart.addBodyPart(attachBodyPart);
            }
        }
        message.setContent(multipart);
        message.saveChanges();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try{
//                        MailSender.message.saveChanges();
//                    }catch(MessagingException e){
//                        Log.i("sendMail","saveChanges");
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        return message;
    }

    //设置获取权限
    public static Properties getProperties(String host) {
//        Properties properties = new Properties();
        Properties properties = System.getProperties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        properties.put("mail.imaps.auth.plain.disable", "true");
        properties.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.imaps.socketFactory.fallback", "false");
        properties.setProperty("mail.imaps.partialfetch", "false");

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        return properties;
    }

    //设置发送权限
    public static Properties sendProperties(String host){
        Properties props = new Properties();

        props.setProperty("mail.transport.protocol","smtp");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");


        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        return props;
    }
}
