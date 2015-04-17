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
 * Asynchronous version of {@link ComposeMailService}, using RxJava.
 *
 * @author Jakob Korherr
 */
@Service
public class ComposeMailServiceAsync {

    @Inject
    @Named("composeMailServiceExecutor")
    private ThreadPoolTaskExecutor composeMailServiceExecutor;

    @Inject
    private ComposeMailService composeMailService;

    public Observable<Mail> openDraft(String uuid, User currentUser) {
        return Observable.<Mail>create((subscriber) -> {
            subscriber.onStart();
            try {
                subscriber.onNext(composeMailService.openDraft(uuid, currentUser));
            } catch (Throwable e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.from(composeMailServiceExecutor));
    }

}
