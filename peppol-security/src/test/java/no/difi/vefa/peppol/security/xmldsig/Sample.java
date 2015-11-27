package no.difi.vefa.peppol.security.xmldsig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author steinar
 *         Date: 23.11.2015
 *         Time: 21.00
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Sample {

    @XmlElement
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


}
