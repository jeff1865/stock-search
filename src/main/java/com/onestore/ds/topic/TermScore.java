package com.onestore.ds.topic;

import lombok.Data;

/**
 * Created by a1000074 on 04/02/2020.
 */
@Data
public class TermScore {
    private String term ;
    private double score ;

    public TermScore(String term, double score) {
        this.term = term;
        this.score = score;
    }

    @Override
    public String toString() {
        return this.term + "(" + this.score + ")" ;
    }

}
