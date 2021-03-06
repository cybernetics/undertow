/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.undertow.server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * A handler which limits the maximum number of concurrent requests.  Requests beyond the limit will
 * block until the previous request is complete.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class RequestLimitingHandler implements HttpHandler {
    private final HttpHandler nextHandler;

    private final RequestLimit requestLimit;

    /**
     * Construct a new instance. The maximum number of concurrent requests must be at least one.  The next handler
     * must not be {@code null}.
     *
     * @param maximumConcurrentRequests the maximum concurrent requests
     * @param nextHandler               the next handler
     */
    public RequestLimitingHandler(int maximumConcurrentRequests, HttpHandler nextHandler) {
        this(maximumConcurrentRequests, -1, nextHandler);
    }

    /**
     * Construct a new instance. The maximum number of concurrent requests must be at least one.  The next handler
     * must not be {@code null}.
     *
     * @param maximumConcurrentRequests the maximum concurrent requests
     * @param queueSize                 the maximum number of requests to queue
     * @param nextHandler               the next handler
     */
    public RequestLimitingHandler(int maximumConcurrentRequests, int queueSize, HttpHandler nextHandler) {
        if (nextHandler == null) {
            throw new IllegalArgumentException("nextHandler is null");
        }
        if (maximumConcurrentRequests < 1) {
            throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
        }
        this.requestLimit = new RequestLimit(maximumConcurrentRequests, queueSize);
        this.nextHandler = nextHandler;
    }

    /**
     * Construct a new instance. This version takes a {@link RequestLimit} directly which may be shared with other
     * handlers.
     *
     * @param requestLimit              the request limit information.
     * @param nextHandler               the next handler
     */
    public RequestLimitingHandler(RequestLimit requestLimit, HttpHandler nextHandler) {
        if (nextHandler == null) {
            throw new IllegalArgumentException("nextHandler is null");
        }
        this.requestLimit = requestLimit;
        this.nextHandler = nextHandler;
    }

    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        requestLimit.handleRequest(exchange, nextHandler);
    }

    public RequestLimit getRequestLimit() {
        return requestLimit;
    }
}
