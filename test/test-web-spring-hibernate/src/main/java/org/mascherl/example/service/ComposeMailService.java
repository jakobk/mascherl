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

import com.github.mauricio.async.db.Configuration;
import com.github.mauricio.async.db.Connection;
import com.github.mauricio.async.db.ResultSet;
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection;
import com.github.mauricio.async.db.postgresql.column.PostgreSQLColumnDecoderRegistry;
import com.github.mauricio.async.db.postgresql.column.PostgreSQLColumnEncoderRegistry;
import com.github.mauricio.async.db.util.ExecutorServiceUtils;
import com.github.mauricio.async.db.util.NettyUtils;
import com.github.pgasync.ConnectionPool;
import com.github.pgasync.ConnectionPoolBuilder;
import io.netty.buffer.PooledByteBufAllocator;
import org.hibernate.jpa.QueryHints;
import org.jadira.usertype.dateandtime.threeten.PersistentZonedDateTime;
import org.jadira.usertype.dateandtime.threeten.columnmapper.TimestampColumnZonedDateTimeMapper;
import org.mascherl.example.domain.Mail;
import org.mascherl.example.domain.MailAddress;
import org.mascherl.example.domain.MailAddressUsage;
import org.mascherl.example.domain.MailType;
import org.mascherl.example.domain.User;
import org.mascherl.example.entity.MailEntity;
import org.mascherl.example.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rx.Observable;
import scala.Some;
import scala.collection.JavaConversions;
import scala.compat.java8.FutureConverters;
import scala.compat.java8.OptionConverters;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.mascherl.example.service.convert.MailConverter.convertToDomain;
import static scala.compat.java8.JFunction.func;

/**
 * Service for composing mails.
 *
 * @author Jakob Korherr
 */
@Service
public class ComposeMailService {

    @PersistenceContext
    private EntityManager em;

    @PersistenceUnit
    private EntityManagerFactory emf;

    private ConnectionPool db;

    @PostConstruct
    public void init() {
        db = new ConnectionPoolBuilder()
                .hostname("localhost")
                .port(5432)
                .database("niotest")
                .username("postgres")
                .password("postgres")
                .poolSize(20)
                .build();

        // just some dummy values or syntax correctness
        String uuid = "uuid";
        User currentUser = new User("Jakob", "Korherr", "jakobk@apache.org");


//        AsyncEntityManager aem = emf.createAsyncEntityManager();
//        aem.beginTransaction()
//                .thenCompose(tx -> tx.createQuery(
//                        "select m " +
//                                "from MailEntity m " +
//                                "where m.uuid = :uuid " +
//                                "and m.user.uuid = :userUuid", MailEntity.class))
//                .thenCompose(query ->
//                    query.setParameter("uuid", uuid)
//                            .setParameter("userUuid", currentUser.getUuid())
//                            .setHint(QueryHints.HINT_READONLY, Boolean.TRUE)
//                            .getResultList())
//                .thenAccept(list -> list.stream().forEach(System.out::println));
    }

    @PreDestroy
    public void close() {
        db.close();
    }

    @Transactional
    public String composeNewMail(User currentUser) {
        MailEntity mailEntity = new MailEntity(MailType.DRAFT);
        mailEntity.setUser(em.getReference(UserEntity.class, currentUser.getUuid()));
        mailEntity.setDateTime(ZonedDateTime.now());
        mailEntity.setUnread(false);
        mailEntity.setFrom(new MailAddress(currentUser.getEmail()));
        em.persist(mailEntity);
        em.flush();

        return mailEntity.getUuid();
    }

    public Mail openDraft(String uuid, User currentUser) {
//        FiniteDuration fiveSeconds = Duration.apply(5l, TimeUnit.SECONDS);
//        Configuration configuration = new Configuration(
//                "postgres",
//                "localhost",
//                5432,
//                new Some<>("postgres"),
//                new Some<>("niotest"),
//                StandardCharsets.UTF_8,
//                16777216,
//                PooledByteBufAllocator.DEFAULT,
//                fiveSeconds,
//                fiveSeconds);
//
//        PostgreSQLConnection postgreSQLConnection = new PostgreSQLConnection(
//                configuration,
//                PostgreSQLColumnEncoderRegistry.Instance(),
//                PostgreSQLColumnDecoderRegistry.Instance(),
//                NettyUtils.DefaultEventLoopGroup(),
//                ExecutorServiceUtils.CachedExecutionContext());
//
//        CompletableFuture<Connection> future = new CompletableFuture<>();
//        postgreSQLConnection.connect().onComplete(func((tryConnection) -> {
//            if (tryConnection.isSuccess()) {
//                future.complete(tryConnection.get());
//            } else {
//                future.completeExceptionally(tryConnection.failed().get());
//            }
//            return future;
//        }), FutureConverters.globalExecutionContext());
//        future.whenComplete((connection, throwable) -> {
//            System.out.println(throwable);
//            System.out.println(connection);
//        });
//
//        FutureConverters.toJava(postgreSQLConnection.connect()
//                .flatMap(func(connection -> connection.sendPreparedStatement("SELECT m.uuid from mail m where m.uuid = ?", JavaConversions.asScalaBuffer(Arrays.asList(uuid)))), FutureConverters.globalExecutionContext())
//                .flatMap(func(result -> {
//                    OptionConverters.toJava(result.rows()).ifPresent((ResultSet rs) -> System.out.println(rs.head().apply(0)));
//                    return postgreSQLConnection.disconnect();
//                }), FutureConverters.globalExecutionContext()))
//                .whenComplete((connection, throwable) -> {
//                    System.out.println(throwable);
//                    System.out.println(connection);
//                });


        List<MailEntity> resultList = em.createQuery(
                "select m " +
                        "from MailEntity m " +
                        "where m.uuid = :uuid " +
                        "and m.user.uuid = :userUuid", MailEntity.class)
                .setParameter("uuid", uuid)
                .setParameter("userUuid", currentUser.getUuid())
                .setHint(QueryHints.HINT_READONLY, Boolean.TRUE)
                .getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        MailEntity entity = resultList.get(0);
        if (entity.getMailType() != MailType.DRAFT) {
            throw new IllegalStateException("Mail with uuid " + uuid + " + is not of type draft");
        }
        return convertToDomain(entity);
    }

    @Transactional
    public void saveDraft(Mail mail, User currentUser) {
        if (mail.getUuid() == null) {
            throw new IllegalArgumentException("Given draft mail has no UUID");
        }

        MailEntity draftEntity = em.find(MailEntity.class, mail.getUuid());
        if (!Objects.equals(draftEntity.getUser().getUuid(), currentUser.getUuid())) {
            throw new IllegalArgumentException("The draft to be saved does not belong to the current user.");
        }
        draftEntity.setMailType(MailType.DRAFT);
        draftEntity.setDateTime(ZonedDateTime.now());
        draftEntity.setUnread(false);
        draftEntity.setTo(mail.getTo());
        draftEntity.setCc(mail.getCc());
        draftEntity.setBcc(mail.getBcc());
        draftEntity.setSubject(mail.getSubject());
        draftEntity.setMessageText(mail.getMessageText());
        em.merge(draftEntity);
        em.flush();
    }

    public List<MailAddressUsage> getLastSendToAddresses(User currentUser, int limit) {
        // unfortunately, due to using an @ElementCollection, this query can only be done natively
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = em.createNativeQuery(
                "select distinct mto.address, m.datetime " +
                        "from mail m " +
                        "join mail_to mto on mto.mail_uuid = m.uuid " +
                        "where m.user_uuid = :userUuid " +
                        "and m.mail_type = :mailTypeSentName " +
                        "and not exists (" +
                        "   select 1 from mail m2 " +
                        "   join mail_to mto2 on mto2.mail_uuid = m2.uuid " +
                        "   where m2.user_uuid = :userUuid " +
                        "   and m2.mail_type = :mailTypeSentName " +
                        "   and mto2.address = mto.address " +
                        "   and m2.datetime > m.datetime " +
                        ") " +
                        "order by m.datetime desc")
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailTypeSentName", MailType.SENT.name())
                .setMaxResults(limit)
                .getResultList();

        TimestampColumnZonedDateTimeMapper dateTimeColumnMapper = new PersistentZonedDateTime().getColumnMapper();
        return resultList.stream()
                .map(row ->
                        new MailAddressUsage(
                                new MailAddress((String) row[0]),
                                convertToZonedDateTimeHibernate(dateTimeColumnMapper, (Timestamp) row[1])))
                .collect(Collectors.toList());
    }

    public Observable<MailAddressUsage> getLastSendToAddressesAsync(User currentUser, int limit) {
        return Observable.<MailAddressUsage>create((subscriber) -> {
            db.query("select distinct mto.address, m.datetime " +
                            "from mail m " +
                            "join mail_to mto on mto.mail_uuid = m.uuid " +
                            "where m.user_uuid = $1 " +
                            "and m.mail_type = $2 " +
                            "and not exists (" +
                            "   select 1 from mail m2 " +
                            "   join mail_to mto2 on mto2.mail_uuid = m2.uuid " +
                            "   where m2.user_uuid = $1 " +
                            "   and m2.mail_type = $2 " +
                            "   and mto2.address = mto.address " +
                            "   and m2.datetime > m.datetime " +
                            ") " +
                            "order by m.datetime desc " +
                            "limit $3",
                    Arrays.asList(currentUser.getUuid(), MailType.SENT.name(), limit),
                    result -> {
                        try {
                            subscriber.onStart();
                            TimestampColumnZonedDateTimeMapper dateTimeColumnMapper = new PersistentZonedDateTime().getColumnMapper();
                            StreamSupport.stream(result.spliterator(), false)
                                    .map(row ->
                                            new MailAddressUsage(
                                                    new MailAddress(row.getString(0)),
                                                    dateTimeColumnMapper.fromNonNullValue(row.getTimestamp(1))))
                                    .forEach(subscriber::onNext);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    },
                    subscriber::onError);
        });
    }

    public CompletableFuture<List<MailAddressUsage>> getLastSendToAddressesAsync2(User currentUser, int limit) {
        CompletableFuture<List<MailAddressUsage>> completableFuture = new CompletableFuture<>();
        db.query("select distinct mto.address, m.datetime " +
                        "from mail m " +
                        "join mail_to mto on mto.mail_uuid = m.uuid " +
                        "where m.user_uuid = $1 " +
                        "and m.mail_type = $2 " +
                        "and not exists (" +
                        "   select 1 from mail m2 " +
                        "   join mail_to mto2 on mto2.mail_uuid = m2.uuid " +
                        "   where m2.user_uuid = $1 " +
                        "   and m2.mail_type = $2 " +
                        "   and mto2.address = mto.address " +
                        "   and m2.datetime > m.datetime " +
                        ") " +
                        "order by m.datetime desc " +
                        "limit $3",
                Arrays.asList(currentUser.getUuid(), MailType.SENT.name(), limit),
                result -> {
                    try {
                        TimestampColumnZonedDateTimeMapper dateTimeColumnMapper = new PersistentZonedDateTime().getColumnMapper();
                        List<MailAddressUsage> usages = StreamSupport.stream(result.spliterator(), false)
                                .map(row ->
                                        new MailAddressUsage(
                                                new MailAddress(row.getString(0)),
                                                dateTimeColumnMapper.fromNonNullValue(row.getTimestamp(1))))
                                .collect(Collectors.toList());
                        completableFuture.complete(usages);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                },
                completableFuture::completeExceptionally);

        return completableFuture;
    }

    public List<MailAddressUsage> getLastReceivedFromAddresses(User currentUser, int limit) {
        return em.createQuery(
                "select distinct new " + MailAddressUsage.class.getName() + "( " +
                        "m.from, " +
                        "m.dateTime" +
                        ") " +
                        "from MailEntity m " +
                        "where m.user.uuid = :userUuid " +
                        "and m.mailType = :mailTypeReceived " +
                        "and not exists ( " +
                        "   select m2.uuid from MailEntity m2 " +
                        "   where m2.user.uuid = :userUuid " +
                        "   and m2.mailType = :mailTypeReceived " +
                        "   and m2.from.address = m.from.address " +
                        "   and m2.dateTime > m.dateTime " +
                        ") " +
                        "order by m.dateTime desc", MailAddressUsage.class)
                .setParameter("userUuid", currentUser.getUuid())
                .setParameter("mailTypeReceived", MailType.RECEIVED)
                .setMaxResults(limit)
                .setHint(QueryHints.HINT_READONLY, Boolean.TRUE)
                .getResultList();
    }

    private static ZonedDateTime convertToZonedDateTimeHibernate(TimestampColumnZonedDateTimeMapper mapper, Timestamp dbTimestamp) {
        // only needed when querying with hibernate, in order to fix a time zone conversion bug
        return mapper.fromNonNullValue(dbTimestamp)
                .withZoneSameLocal(ZoneId.of("Z"))
                .withZoneSameInstant(ZoneId.systemDefault());
    }

}
