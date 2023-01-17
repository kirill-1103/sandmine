package ru.krey.sandmine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.Set;

@Node("WORKER")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Worker {
    @Id
    @GeneratedValue
    private Long id;

    @Property(name = "surname")
    private String surname;

    @Property(name = "name")
    private String name;

    @Property(name = "patronymic")
    private String patronymic;

    @Property(name = "email")
    private String email;

    @Property(name = "phone_number")
    private String phoneNumber;

    @Property(name = "passport")
    private String passport;

    @Property(name = "role")
    private String role;

    @Property(name = "pass_id")
    private Long passId;

    @Property(name = "password")
    private String password;

    @Relationship(type = "HAS_ACCESS_TO", direction = Relationship.Direction.OUTGOING)
    private Set<Zone> zonesWithAccess;

    @Relationship(type = "HAS_SHIFT", direction = Relationship.Direction.OUTGOING)
    private Set<Shift> shifts;
}
