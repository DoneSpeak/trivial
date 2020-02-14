package donespeak.com.mail.someClass;

import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import donespeak.com.mail.MainActivity;
import donespeak.com.mail.bean.MailBean;

/**
 * Created by glorior on 2016/6/21.
 */
public class getInboxMessagesListRunnable implements Runnable {

    private Message[] messages;
    private MailBean mailBean;
    private List<MailBean> mailBeanList;

    public getInboxMessagesListRunnable(Message[] messages){
        this.messages = messages;
    }

    @Override
    public void run(){
        String from;
        String subject;
        Date sentDate;
        for(int i = 0; i < messages.length; i ++){
            try {
                from =  messages[i].getFrom()[0].toString();
                subject = messages[i].getSubject();
                sentDate = messages[i].getSentDate();
            } catch (MessagingException e) {
                //TODO 异常处理
                if(MainActivity.DEBUG) {
                    e.printStackTrace();
                }
            }
            mailBean = new MailBean();
            mailBeanList.add(mailBean);

        }
    }
}
