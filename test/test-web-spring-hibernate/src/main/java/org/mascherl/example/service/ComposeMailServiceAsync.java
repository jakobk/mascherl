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
package org.mascherl.example.service;

import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddressUsage;
import org.mascherl.example.domain.User;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

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

    public Observable<List<MailAddressUsage>> getLastReceivedFromAddresses(User currentUser, int limit) {
        return Observable.<List<MailAddressUsage>>create((subscriber) -> {
            subscriber.onStart();
            try {
                subscriber.onNext(composeMailService.getLastReceivedFromAddresses(currentUser, limit));
            } catch (Throwable e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.from(composeMailServiceExecutor));
    }

    public Observable<List<MailAddressUsage>> getLastSendToAddresses(User currentUser, int limit) {
        return Observable.<List<MailAddressUsage>>create((subscriber) -> {
            subscriber.onStart();
            try {
                subscriber.onNext(composeMailService.getLastSendToAddresses(currentUser, limit));
            } catch (Throwable e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.from(composeMailServiceExecutor));
    }

}
