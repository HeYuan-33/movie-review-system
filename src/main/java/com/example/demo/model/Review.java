package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonBackReference  
    private Movie movie;
    
    private String content;
    private Integer rating;
    private Long timestamp;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Reply> replies;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public List<Reply> getReplies() { return replies; }
    public void setReplies(List<Reply> replies) { this.replies = replies; }
}
