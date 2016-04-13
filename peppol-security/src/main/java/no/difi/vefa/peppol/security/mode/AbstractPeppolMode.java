package no.difi.vefa.peppol.security.mode;

import no.difi.vefa.peppol.common.code.Service;
import no.difi.vefa.peppol.security.api.ModeDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class AbstractPeppolMode implements ModeDescription {

    @Override
    public String[] getIssuers(Service service) {
        switch (service) {
            case ALL:
                List<String> issuers = new ArrayList<>();
                for (Service s : Service.values())
                    if (s != Service.ALL)
                        Collections.addAll(issuers, getIssuers(s));
                return issuers.toArray(new String[issuers.size()]);
            default:
                return getIssuersInternal(service);
        }
    }

    protected abstract String[] getIssuersInternal(Service service);
}
