package ru.krey.sandmine.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("ZONE")
public class Zone {
    @Id
    @GeneratedValue
    private Long id;

    @Property(name = "name")
    private String name;
}
