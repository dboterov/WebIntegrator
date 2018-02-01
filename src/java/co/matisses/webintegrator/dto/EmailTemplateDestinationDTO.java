package co.matisses.webintegrator.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author dbotero
 */
public class EmailTemplateDestinationDTO {

    private String templateName;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;

    public EmailTemplateDestinationDTO() {
        to = new ArrayList<>();
        cc = new ArrayList<>();
        bcc = new ArrayList<>();
    }

    public EmailTemplateDestinationDTO(String templateName, String to, String cc, String bcc) {
        this.to = new ArrayList<>();
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();
        this.templateName = templateName;
        if (to != null) {
            this.to.add(to);
        }
        if (cc != null) {
            this.cc.add(cc);
        }
        if (bcc != null) {
            this.bcc.add(bcc);
        }
    }

    public void addDestinations(String to, String cc, String bcc) {
        if (to != null) {
            this.to.add(to);
        }
        if (cc != null) {
            this.cc.add(cc);
        }
        if (bcc != null) {
            this.bcc.add(bcc);
        }
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.templateName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmailTemplateDestinationDTO other = (EmailTemplateDestinationDTO) obj;
        if (!Objects.equals(this.templateName, other.templateName)) {
            return false;
        }
        return true;
    }
}
