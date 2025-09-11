package org.clevercastle.authforge.http;

import org.clevercastle.authforge.exception.CastleException;

public interface IHttpClient {
    HttpResponse execute(HttpRequest request) throws CastleException;
}
