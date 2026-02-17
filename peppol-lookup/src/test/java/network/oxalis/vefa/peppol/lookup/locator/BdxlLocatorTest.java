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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Type;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import static org.testng.Assert.*;

public class BdxlLocatorTest {

    private BdxlLocator locator;

    @BeforeClass
    public void setUp() {
        locator = new BdxlLocator(Mode.of("TEST"));
    }

    @Test
    public void simple() throws Exception {
        Assert.assertEquals(
                locator.lookup(ParticipantIdentifier.of("0192:923829644")),
                URI.create("https://test-smp.elma-smp.no")
        );
    }

    @Test
    public void testRegexHandler() {
        // BDXL Specification (modified)
        Assert.assertEquals(
                BdxlLocator.handleRegex(
                        // "!^B-(+[0-9a-fA-F]).sid.peppol.eu$!https://serviceprovider.peppol.eu/\\1!",
                        "!^B-([0-9a-fA-F]+).sid.peppol.eu$!https://serviceprovider.peppol.eu/\\\\1!",
                        "B-eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565.sid.peppol.eu"
                ),
                "https://serviceprovider.peppol.eu/eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565"
        );

        // Proper
        Assert.assertEquals(
                BdxlLocator.handleRegex(
                        "!^.*$!http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu!",
                        "B-eacf0eecc06f3fe1cff9e0e674201d99fc73affaf5aa6eccd3a30565.iso6523-actorid-upis." +
                                "acc.edelivery.tech.ec.europa.eu"
                ),
                "http://test-smp.difi.no.publisher.acc.edelivery.tech.ec.europa.eu"
        );
    }


    // ════════════════════════════════════════════════════════════
    // Exception Type Contracts — Unregistered Participants
    // ════════════════════════════════════════════════════════════

    @Test(groups = "integration")
    public void lookup_unregisteredParticipant_throwsPeppolResourceException() {
        String identifier = "9999:does-not-exist-bdxl-test";

        try {
            locator.lookup(ParticipantIdentifier.of(identifier));
            fail("Expected PeppolResourceException for unregistered participant");
        } catch (PeppolResourceException e) {
            assertEquals(e.getMessage(),
                    String.format("Participant with identifier '%s' is not registered in SML. The host does not exist", identifier));
        } catch (PeppolInfrastructureException e) {
            System.err.printf("WARNING: Got PeppolInfrastructureException — likely a network issue, not a regression. Message: %s%n", e.getMessage());
        } catch (LookupException e) {
            fail(String.format("Expected PeppolResourceException but got %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Ignore
    @Test(groups = "integration")
    public void lookup_registeredParticipantNoRecords_throwsPeppolResourceException() {
        String identifier = "0201:doxee_test";

        try {
            locator.lookup(ParticipantIdentifier.of(identifier));
            fail("Expected PeppolResourceException for unregistered participant");
        } catch (PeppolResourceException e) {
            assertEquals(e.getMessage(),
                    String.format("Participant with identifier '%s' is not registered in SML. " +
                            "The Host exists, but has no records associated with the queried type", identifier));
        } catch (LookupException e) {
            fail(String.format("Expected PeppolResourceException but got %s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }


    // ════════════════════════════════════════════════════════════
    // Reflection Tests for Private Methods
    // ════════════════════════════════════════════════════════════

    @DataProvider(name = "lookupFailureCodes")
    public Object[][] lookupFailureCodes() {
        return new Object[][]{
                {Lookup.HOST_NOT_FOUND, PeppolResourceException.class, "The host does not exist"},
                {Lookup.TYPE_NOT_FOUND, PeppolResourceException.class, "The Host exists, but has no records associated with the queried type"},
                {Lookup.TRY_AGAIN, PeppolInfrastructureException.class, "due to network error. Retry may help"},
                {Lookup.UNRECOVERABLE, PeppolInfrastructureException.class, "due to a data or server error"},
        };
    }

    @Test(dataProvider = "lookupFailureCodes")
    public void testHandleLookupFailureUsingReflection(int dnsResultCode,
                                                       Class<? extends Exception> expectedException,
                                                       String partialMessage) throws Exception {
        // 1. Prepare Arguments
        String identifier = "0088:123456";
        ParticipantIdentifier participant = ParticipantIdentifier.of(identifier);

        // We need a Lookup object. Since we can't easily mock the final getResult()
        // without PowerMock, we use your helper to create a real Lookup with a mocked result.
        Lookup mockLookup = createLookupWithResult(dnsResultCode);

        // 2. Use Reflection to get the private method
        Method method = BdxlLocator.class.getDeclaredMethod("handleLookupFailure",
                Lookup.class, ParticipantIdentifier.class);
        method.setAccessible(true);

        // 3. Invoke and Catch
        try {
            method.invoke(locator, mockLookup, participant);
            fail("Method should have thrown an exception");
        } catch (InvocationTargetException e) {
            // Reflection wraps the actual exception in an InvocationTargetException
            Throwable cause = e.getCause();

            assertEquals(cause.getClass(), expectedException,
                    "Should throw the correct exception type for code: " + dnsResultCode);
            assertTrue(cause.getMessage().contains(partialMessage),
                    "Error message should contain expected text");
            assertTrue(cause.getMessage().contains(identifier),
                    "Error message should contain the participant identifier");
        }
    }


    // ════════════════════════════════════════════════════════════
    // FileNotFoundException Regression Guard (#497, #666)
    // ════════════════════════════════════════════════════════════

    @Test(groups = "integration")
    public void lookup_unregisteredParticipant_neverThrowsFileNotFoundException() {
        try {
            locator.lookup(ParticipantIdentifier.of("9999:fnf-rg-bdxl"));
            fail("Expected PeppolResourceException for unregistered participant");
        } catch (LookupException e) {
            Throwable current = e;
            while (current != null) {
                assertFalse(current instanceof FileNotFoundException,
                        String.format("FileNotFoundException must NEVER appear in the exception chain. Found: %s: %s",
                                current.getClass().getName(), current.getMessage()));
                current = current.getCause();
            }
        }
    }


    private Lookup createLookupWithResult(int resultCode) throws Exception {
        Lookup lookup = new Lookup("test.example.com", Type.NAPTR);
        lookup.run();

        Field resultField = Lookup.class.getDeclaredField("result");
        resultField.setAccessible(true);
        resultField.setInt(lookup, resultCode);

        // Verify value is unchanged
        assertEquals(lookup.getResult(), resultCode, "Result field override failed");

        return lookup;
    }

}
