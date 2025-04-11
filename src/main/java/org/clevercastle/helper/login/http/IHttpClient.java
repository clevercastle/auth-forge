package org.clevercastle.helper.login.http;

import org.clevercastle.helper.login.CastleException;

public interface IHttpClient {
    HttpResponse execute(HttpRequest request) throws CastleException;
}
