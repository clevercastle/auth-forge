package org.clevercastle.authforge.http;

import org.clevercastle.authforge.CastleException;

public interface IHttpClient {
    HttpResponse execute(HttpRequest request) throws CastleException;
}
