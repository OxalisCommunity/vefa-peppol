package no.difi.vefa.peppol.common.model;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author erlend
 */
public interface Period {

    Date getFrom();

    Date getTo();

    default boolean isCurrent(Date date) {
        return (getFrom() == null || date.after(getFrom())) && (getTo() == null || date.before(getTo()));
    }

    default boolean isCurrent() {
        return isCurrent(new Date());
    }

    static Period of(Date from, Date to) {
        return new DefaultPeriod(from, to);
    }

    @Getter
    @ToString
    class DefaultPeriod implements Period, Serializable {

        private static final long serialVersionUID = 888582195965219162L;

        private Date from;

        private Date to;

        private DefaultPeriod(Date from, Date to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Period period = (Period) o;
            return Objects.equals(from, period.getFrom()) &&
                    Objects.equals(to, period.getTo());
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }
}
