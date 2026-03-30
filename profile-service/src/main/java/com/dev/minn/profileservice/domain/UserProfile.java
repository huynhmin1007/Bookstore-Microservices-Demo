package com.dev.minn.profileservice.domain;

import com.dev.minn.profileservice.utils.UuidV7Generator;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.Objects;

@Node("UserProfile")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
@Setter
public class UserProfile {

    @Id
    @GeneratedValue(UuidV7Generator.class)
    String id;

    @Property("userId")
    String userId;

    @Property("firstName")
    String firstName;

    @Property("lastName")
    String lastName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
