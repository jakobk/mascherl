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
package org.mascherl.example.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;

/**
 * Base class for our entities, contains id and version handling.
 *
 * @author Jakob Korherr
 */
@MappedSuperclass
public abstract class BaseEntity {

    public static final String UUID = "uuid";
    public static final String VERSION = "version";

    private static final int UUID_LENGTH = 32;

    @Id
    @Column(name = UUID, length = UUID_LENGTH)
    private String uuid;

    @Version
    @Column(name = VERSION, nullable = false)
    private Long version;

    protected BaseEntity(String uuid) {
        this.uuid = uuid;
    }

    protected BaseEntity() {
    }

    public String getUuid() {
        if (uuid == null) {
            createUUid();
        }
        return uuid;
    }

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            createUUid();
        }
    }

    private void createUUid() {
        this.uuid = newUuid();
    }

    private static String newUuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;

        BaseEntity that = (BaseEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
