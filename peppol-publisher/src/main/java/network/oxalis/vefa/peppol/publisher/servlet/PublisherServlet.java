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

package network.oxalis.vefa.peppol.publisher.servlet;

import lombok.extern.slf4j.Slf4j;
import network.oxalis.vefa.peppol.common.lang.PeppolParsingException;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.util.ModelUtils;
import network.oxalis.vefa.peppol.publisher.PublisherService;
import network.oxalis.vefa.peppol.publisher.lang.PublisherException;
import network.oxalis.vefa.peppol.publisher.lang.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author erlend
 */
@Slf4j
public class PublisherServlet extends HttpServlet {

    private static final Pattern PATH_SERVICE_GROUP =
            Pattern.compile("^/([a-z0-9\\-]+::[a-z0-9:]+)$");

    private static final Pattern PATH_SERVICE_METADATA =
            Pattern.compile("^/([a-z0-9\\-]+::[a-z0-9:]+)/services/([a-z0-9\\-]+::.+)$");

    private PublisherService publisherService;

    public PublisherServlet(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String path = ModelUtils.urldecode(req.getRequestURI());

            Matcher matcher = PATH_SERVICE_METADATA.matcher(path);
            if (matcher.matches()) {
                handleMetadataProvider(req, resp, matcher.group(1), matcher.group(2));
                return;
            }

            matcher = PATH_SERVICE_GROUP.matcher(path);
            if (matcher.matches()) {
                handleServiceGroup(req, resp, matcher.group(1));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (PeppolParsingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (JAXBException | IOException | NullPointerException | PublisherException e) {
            log.error(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void handleServiceGroup(HttpServletRequest req, HttpServletResponse resp, String participantParam)
            throws IOException, PeppolParsingException, JAXBException, PublisherException {
        ParticipantIdentifier participantIdentifier = ParticipantIdentifier.parse(participantParam);

        resp.setContentType("text/xml");
        publisherService.serviceGroup(resp.getOutputStream(), req.getParameter("syntax"),
                URI.create(createPublisherRoot(req)), participantIdentifier);
    }

    public void handleMetadataProvider(HttpServletRequest req, HttpServletResponse resp,
                                       String participantParam, String documentTypeParam)
            throws IOException, PeppolParsingException, JAXBException, PublisherException {
        ParticipantIdentifier participantIdentifier = ParticipantIdentifier.parse(participantParam);
        DocumentTypeIdentifier documentTypeIdentifier = DocumentTypeIdentifier.parse(documentTypeParam);

        resp.setContentType("text/xml");
        publisherService.metadataProvider(resp.getOutputStream(), req.getParameter("syntax"),
                participantIdentifier, documentTypeIdentifier);
    }

    private String createPublisherRoot(HttpServletRequest req) {
        if ((req.getServerPort() == 80 && req.getScheme().equalsIgnoreCase("http")) ||
                (req.getServerPort() == 443 && req.getScheme().equalsIgnoreCase("https"))) {
            return req.getScheme() + "://" + req.getServerName() + "/";
        } else {
            return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/";
        }
    }
}
