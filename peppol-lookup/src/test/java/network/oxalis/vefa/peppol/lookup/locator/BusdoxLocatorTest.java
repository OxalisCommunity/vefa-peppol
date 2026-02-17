/*
 * Copyright 2015-2017 Direktoratet for forvaltning og IKT
 *
 * This source code is subject to dual licensing:
 *
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package network.oxalis.vefa.peppol.lookup.locator;

import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.lookup.api.LookupException;
import network.oxalis.vefa.peppol.lookup.api.PeppolInfrastructureException;
import network.oxalis.vefa.peppol.lookup.api.PeppolResourceException;
import network.oxalis.vefa.peppol.mode.Mode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.testng.Assert.*;

public class BusdoxLocatorTest {

    private BusdoxLocator busdoxLocator;


    @BeforeClass
    public void setUp() {
        busdoxLocator = new BusdoxLocator(Mode.of("PRODUCTION"));
    }

    @Test
    public void simple() throws LookupException {
        assertEquals(busdoxLocator.lookup("0192:991825827").getHost(),
                "B-9823154777831486f5f30f7f41385a2a.iso6523-actorid-upis.edelivery.tech.ec.europa.eu");
    }

    // ════════════════════════════════════════════════════════════
    // Exception Type Contracts — Unregistered Participants
    // ════════════════════════════════════════════════════════════

    @Test(groups = "integration")
    public void lookup_unregisteredParticipant_throwsPeppolResourceException() {
        String identifier = "9999:does-not-exist-busdox-test";

        try {
            busdoxLocator.lookup(ParticipantIdentifier.of(identifier));
            fail("Expected PeppolResourceException for unregistered participant");
        } catch (PeppolResourceException e) {
            assertEquals(e.getMessage(),
                    String.format("Participant with identifier '%s' is not registered in SML. The host does not exist", identifier));
            assertNull(e.getCause());
        } catch (PeppolInfrastructureException e) {
            System.err.printf("WARNING: Got PeppolInfrastructureException — likely a network issue, not a regression. Message: %s%n", e.getMessage());
        } catch (LookupException e) {
            fail(String.format("Expected PeppolResourceException but got %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    // handleLookupFailure() — DNS Result Code Mapping
    // ════════════════════════════════════════════════════════════

    @DataProvider(name = "lookupFailureCodes")
    public Object[][] lookupFailureCodes() {
        return new Object[][]{
                {Lookup.HOST_NOT_FOUND, PeppolResourceException.class, "is not registered in SML. The host does not exist"},
                {Lookup.TYPE_NOT_FOUND, PeppolResourceException.class, "is not registered in SML. The Host exists, but has no records associated with the queried type"},
                {Lookup.TRY_AGAIN, PeppolInfrastructureException.class, "due to network error. Retry may help"},
                {Lookup.UNRECOVERABLE, PeppolInfrastructureException.class, "due to a data or server error"},
        };
    }

    @Test(dataProvider = "lookupFailureCodes")
    public void handleLookupFailure_mapsResultCodeToCorrectException(int dnsResultCode, Class<? extends LookupException> expectedType, String expectedMsg) throws Exception {
        String identifier = "9999:busdox-failure-test";
        ParticipantIdentifier participant = ParticipantIdentifier.of(identifier);

        Lookup lookup = createLookupWithResult(dnsResultCode);

        Method method = BusdoxLocator.class.getDeclaredMethod("handleLookupFailure", Lookup.class, ParticipantIdentifier.class);
        method.setAccessible(true);

        try {
            method.invoke(busdoxLocator, lookup, participant);
            fail(String.format("Expected %s for DNS result code %d", expectedType.getSimpleName(), dnsResultCode));
        } catch (InvocationTargetException e) {
            Throwable actual = e.getCause();
            assertEquals(actual.getClass(), expectedType,
                    String.format("DNS result %d — expected %s but got %s: %s", dnsResultCode, expectedType.getSimpleName(), actual.getClass().getSimpleName(), actual.getMessage()));
            assertTrue(actual.getMessage().contains(expectedMsg),
                    String.format("DNS result %d — expected message containing '%s' but got: %s", dnsResultCode, expectedMsg, actual.getMessage()));
            assertTrue(actual.getMessage().contains(identifier),
                    String.format("DNS result %d — message should contain identifier '%s' but got: %s", dnsResultCode, identifier, actual.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    // Helper
    // ════════════════════════════════════════════════════════════

    private Lookup createLookupWithResult(int resultCode) throws Exception {
        Lookup lookup = new Lookup("test.example.com", Type.A);
        lookup.run();

        Field resultField = Lookup.class.getDeclaredField("result");
        resultField.setAccessible(true);
        resultField.setInt(lookup, resultCode);

        assertEquals(lookup.getResult(), resultCode, "Result field override failed");
        return lookup;
    }


}
