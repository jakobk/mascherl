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
