package Models;

public class ParentAccount {
    String Name,Country,State,ID,Mail,Pass,Linkchild;

    public ParentAccount() {
        Linkchild = null;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountry() {
        return Country;
    }

    public String getState() {
        return State;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public void setPass(String pass) {
        Pass = pass;
    }

    public void setLinkchild(String linkchild) {
        Linkchild = linkchild;
    }

    public String getName() {
        return Name;
    }

    public String getID() {
        return ID;
    }

    public String getMail() {
        return Mail;
    }

    public String getPass() {
        return Pass;
    }

    public String getLinkchild() {
        return Linkchild;
    }
}
