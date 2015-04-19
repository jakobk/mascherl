/*
 * Copyright 2015, Jakob Korherr
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
package org.mascherl.servlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet filter for storing the current request and response objects for later access from Mascherl.
 *
 * @author Jakob Korherr
 */
@WebFilter(
        urlPatterns = "/*",
        asyncSupported = true,
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.ASYNC})
public class MascherlFilter implements Filter {

    private static ThreadLocal<HttpServletRequest> requestThreadLocal;
    private static ThreadLocal<HttpServletResponse> responseThreadLocal;

    public static HttpServletRequest getRequest() {
        return requestThreadLocal.get();
    }

    public static HttpServletResponse getResponse() {
        return responseThreadLocal.get();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        requestThreadLocal = new ThreadLocal<>();
        responseThreadLocal = new ThreadLocal<>();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            requestThreadLocal.set((HttpServletRequest) request);
        }
        if (response instanceof HttpServletResponse) {
            responseThreadLocal.set((HttpServletResponse) response);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            requestThreadLocal.remove();
            responseThreadLocal.remove();
        }
    }

    @Override
    public void destroy() {
        requestThreadLocal = null;
        responseThreadLocal = null;
    }

}
