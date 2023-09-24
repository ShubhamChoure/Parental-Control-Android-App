package Models;

public class ParentAccount {
    String Mail,LinkChild;

    public ParentAccount() {
        LinkChild = null;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public void setLinkchild(String linkchild) {
        LinkChild = linkchild;
    }

    public String getMail() {
        return Mail;
    }

    public String getLinkchild() {
        return LinkChild;
    }
}
