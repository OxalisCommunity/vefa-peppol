package network.oxalis.vefa.peppol.common.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author erlend
 */
public class ServiceReference {

    private DocumentTypeIdentifier documentTypeIdentifier;

    private Set<ProcessIdentifier> processIdentifiers;

    public static ServiceReference of(DocumentTypeIdentifier documentTypeIdentifier, ProcessIdentifier... processIdentifiers) {
        return new ServiceReference(documentTypeIdentifier, new HashSet<>(Arrays.asList(processIdentifiers)));
    }

    public static ServiceReference of(DocumentTypeIdentifier documentTypeIdentifier, Set<ProcessIdentifier> processIdentifiers) {
        return new ServiceReference(documentTypeIdentifier, processIdentifiers);
    }

    private ServiceReference(DocumentTypeIdentifier documentTypeIdentifier, Set<ProcessIdentifier> processIdentifiers) {
        this.documentTypeIdentifier = documentTypeIdentifier;
        this.processIdentifiers = processIdentifiers;
    }

    public DocumentTypeIdentifier getDocumentTypeIdentifier() {
        return documentTypeIdentifier;
    }

    public Set<ProcessIdentifier> getProcessIdentifiers() {
        return processIdentifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceReference that = (ServiceReference) o;
        return Objects.equals(documentTypeIdentifier, that.documentTypeIdentifier) &&
                Objects.equals(processIdentifiers, that.processIdentifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentTypeIdentifier, processIdentifiers);
    }
}
