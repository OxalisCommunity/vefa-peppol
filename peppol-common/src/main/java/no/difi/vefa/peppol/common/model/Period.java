package no.difi.vefa.peppol.common.model;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author erlend
 */
@Getter
@ToString
public class Period implements Serializable {

    private static final long serialVersionUID = 888582195965219162L;

    private Date from;

    private Date to;

    public static Period of(Date from, Date to) {
        return new Period(from, to);
    }

    private Period(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return Objects.equals(from, period.from) &&
                Objects.equals(to, period.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
