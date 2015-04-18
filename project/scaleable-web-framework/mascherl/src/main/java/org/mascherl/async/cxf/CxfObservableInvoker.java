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
 * Custom JAX-RS invoker for CXF, that handles {@link rx.Observable} return values of resource methods by
 * creating an {@link javax.ws.rs.container.AsyncResponse}.
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
