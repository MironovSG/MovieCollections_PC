package ru.mironov.moviecollections.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "moviedetails")
public class MovieDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long detailId;
    @Column(nullable = false)
    private float duration;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Format format;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Publisher publisher;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Country country;
    @ManyToOne(fetch = FetchType.LAZY)
    private  Movie movie;

}
