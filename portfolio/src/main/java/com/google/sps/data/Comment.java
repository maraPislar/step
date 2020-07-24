/**
* A comment has an id, text, an author and a mood.
*/

package com.google.sps.data;

public final class Comment {
  public final long id;
  public final long timestamp;
  public final String text;
  public final String author;
  public final String mood;

  public Comment(long id, String text, String author, String mood, long timestamp) {
    this.id = id;
    this.text = text;
    this.author = author;
    this.mood = mood;
    this.timestamp = timestamp;
  }
}