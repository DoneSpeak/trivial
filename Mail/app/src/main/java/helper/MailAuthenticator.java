package helper;

/**
 * Created by glorior on 2016/6/21.
 */

import javax.mail.PasswordAuthentication;
import javax.mail.Authenticator;

public class MailAuthenticator extends Authenticator {

    String username;
    String password;

    public MailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

}