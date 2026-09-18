package com.gilbert.loadtest.song;

import jakarta.persistence.*;

@Entity
@Table(name = "artist")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    protected Artist() {}

    public Long getId() { return id; }
    public String getName() { return name; }
}
