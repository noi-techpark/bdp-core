// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later
package com.opendatahub.timeseries.bdp.writer.writer.authz;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotAuthorizedException extends ResponseStatusException {
    public NotAuthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
