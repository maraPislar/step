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
  public final String email;

  public Comment(long id, String text, String author, String mood, long timestamp,
                String email) {
    this.id = id;
    this.text = text;
    this.author = author;
    this.mood = mood;
    this.timestamp = timestamp;
    this.email = email;
  }

  public long getTimestamp() {
      return this.timestamp;
  }

  public int getTextLength() {
      return (Integer) this.text.length();
  }
}