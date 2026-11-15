package com.matchiq.matchiq.entity;

import org.apache.tomcat.util.http.parser.TE;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teams")

public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String country;
    private String league;
    private Integer externalApiId;
    public Team(){

    }
    public Team(String name, String country, String league, int externalApiId){
        this.name =name;
        this.country = country;
        this.league = league;
        this.externalApiId = externalApiId;
    }
    public Long getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getCountry(){
        return country;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public String getLeague(){
        return league;
    }
    public void setLeague(String league){
        this.league = league;
    }
    public Integer getExternalApiId(){
        return externalApiId;
    }
    public void setExternalApiId(Integer externalApiId){
        this.externalApiId = externalApiId;
    }
}
