package com.movies_searcher.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "watch_list")
@Setter
@Getter
@NoArgsConstructor
public class WatchList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private Long tmdbId;
    @Column(nullable = false, length = 10)
    private String videoType;
    @Column(nullable = false, length = 500)
    private String posterPath;
    @Column(nullable = false, length = 500)
    private String title;
    public WatchList(User user, Long tmdbId, String videoType, String posterPath, String title) {
        this.user = user;
        this.tmdbId = tmdbId;
        this.videoType = videoType;
        this.posterPath = posterPath;
        this.title = title;
    }
}
