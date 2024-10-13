package com.example.goodeats9;

//-----------------------------------------IM/2021/0562 - Hasindu ---------------------------------------------------//
public class Review {
    private String name;
    private String comment;
    private String timestamp;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String name, String comment, String timestamp) {
        this.name = name;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
//-----------------------------------------IM/2021/050 - Kavishi ---------------------------------------------------//
