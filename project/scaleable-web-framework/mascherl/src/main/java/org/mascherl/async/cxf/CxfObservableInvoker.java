package org.mascherl.async.cxf;

import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.jaxrs.impl.AsyncResponseImpl;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.MessageContentsList;
import rx.Observable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * TODO
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

                    observable.subscribe(
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
