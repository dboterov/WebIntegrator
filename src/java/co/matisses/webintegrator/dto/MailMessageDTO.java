package co.matisses.webintegrator.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dbotero
 */
public class MailMessageDTO {

    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;

    public MailMessageDTO() {
        to = new ArrayList<>();
        cc = new ArrayList<>();
        bcc = new ArrayList<>();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public String getToList() {
        StringBuilder sb = new StringBuilder();
        for (String address : to) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(address);
        }
        return sb.toString();
    }

    public void addToAddress(String to) {
        this.to.add(to);
    }

    public List<String> getCc() {
        return cc;
    }

    public String getCcList() {
        StringBuilder sb = new StringBuilder();
        for (String address : cc) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(address);
        }
        return sb.toString();
    }

    public void addCcAddress(String cc) {
        this.cc.add(cc);
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getBccList() {
        StringBuilder sb = new StringBuilder();
        for (String address : bcc) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(address);
        }
        return sb.toString();
    }

    public void addBccAddress(String bcc) {
        this.bcc.add(bcc);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void addToAddresses(List<String> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            to.addAll(addresses);
        }
    }

    public void addCcAddresses(List<String> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            cc.addAll(addresses);
        }
    }

    public void addBccAddresses(List<String> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            bcc.addAll(addresses);
        }
    }

    @Override
    public String toString() {
        return "MailMessageDTO{" + "from=" + from + ", to=" + to + ", cc=" + cc + ", bcc=" + bcc + ", subject=" + subject + '}';
    }
}
