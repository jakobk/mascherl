package org.mascherl.example.service;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.User;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Asynchronous version of {@link SendMailService}, using RxJava.
 *
 * @author Jakob Korherr
 */
@Service
public class SendMailServiceAsync {

    @Inject
    @Named("sendMailServiceExecutor")
    private ThreadPoolTaskExecutor sendMailServiceExecutor;

    @Inject
    private SendMailService sendMailService;

    public Observable<Void> sendMail(Mail mail, User currentUser) {
        return Observable.<Void>create((subscriber) -> {
            subscriber.onStart();
            try {
                sendMailService.sendMail(mail, currentUser);
                subscriber.onNext(null);
            } catch (Throwable e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.from(sendMailServiceExecutor));
    }

}
