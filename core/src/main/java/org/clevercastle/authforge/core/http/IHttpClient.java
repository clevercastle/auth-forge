package org.clevercastle.authforge.core.http;

import org.clevercastle.authforge.core.exception.CastleException;

public interface IHttpClient {
    HttpResponse execute(HttpRequest request) throws CastleException;
}
