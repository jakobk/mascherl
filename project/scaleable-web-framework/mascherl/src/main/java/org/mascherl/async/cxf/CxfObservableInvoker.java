package org.mascherl.async.cxf;

import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.jaxrs.impl.AsyncResponseImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.MessageContentsList;
import rx.Observable;
import rx.schedulers.Schedulers;

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

                    AsyncResponseImpl asyncResponse = new AsyncResponseImpl(exchange.getInMessage());
                    asyncResponse.suspendContinuationIfNeeded();

                    observable
                            .subscribeOn(Schedulers.computation())
                            .subscribe(
                                    (entity) -> {
                                        asyncResponse.resume(entity);
                                    },
                                    (error) -> {
                                        asyncResponse.resume(error);
                                    }
                            );

                    return new MessageContentsList(Collections.singletonList(null));  // like the method returned void
                }
            }
        }
        return result;
    }

}
