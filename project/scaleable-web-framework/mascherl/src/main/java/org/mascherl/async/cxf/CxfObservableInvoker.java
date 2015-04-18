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
package org.mascherl.async.cxf;

import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.jaxrs.impl.AsyncResponseImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.MessageContentsList;
import rx.Observable;
import rx.internal.util.ScalarSynchronousObservable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * CXF specific JAX-RS invoker, that handles {@link rx.Observable} return values of resource methods by
 * creating an {@link javax.ws.rs.container.AsyncResponse}.
 *
 * This can currently not be done with plain JAX-RS api.
 *
 * Register via the following code in your spring.xml:
 * <pre>
 *     <jaxrs:invoker>
 *         <bean class="org.mascherl.async.cxf.CxfObservableInvoker" />
 *     </jaxrs:invoker>
 * </pre>
 *
 * @author Jakob Korherr
 */
public class CxfObservableInvoker extends JAXRSInvoker {

    @Override
    protected Object invoke(Exchange exchange, Object serviceObject, Method m, List<Object> params) {
        Object result = super.invoke(exchange, serviceObject, m, params);
        if (result instanceof MessageContentsList) {
            MessageContentsList contentsList = (MessageContentsList) result;
            if (!contentsList.isEmpty()) {
                Object responseEntity = contentsList.get(0);
                if (responseEntity instanceof Observable) {
                    Observable<?> observable = (Observable<?>) responseEntity;

                    if (observable instanceof ScalarSynchronousObservable) {
                        ScalarSynchronousObservable syncObservable = (ScalarSynchronousObservable) observable;
                        return new MessageContentsList(syncObservable.get());  // as if the method returned the value directly
                    } else {
                        // start asynchronous processing
                        AsyncResponseImpl asyncResponse = new AsyncResponseImpl(exchange.getInMessage());
                        asyncResponse.suspendContinuationIfNeeded();
                        observable
                                .subscribe(
                                        (entity) -> asyncResponse.resume(entity),
                                        (error) -> asyncResponse.resume(error)
                                );
                        return new MessageContentsList(Collections.singletonList(null));  // as if the method returned void
                    }
                }
            }
        }
        return result;
    }

}
