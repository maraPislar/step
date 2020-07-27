/**
* A comment has an id, text, an author and a mood.
*/

package com.google.sps.data;

public final class Comment {
  public final long id;
  public final Long timestamp;
  public final String text;
  public final String author;
  public final String mood;
  public final String filter;

  public Comment(long id, String text, String author, String mood, Long timestamp,
                String filter) {
    this.id = id;
    this.text = text;
    this.author = author;
    this.mood = mood;
    this.timestamp = timestamp;
    this.filter = filter;
  }

  public Long getTimestamp() {
      return this.timestamp;
  }

  public Integer getTextLength() {
      return (Integer) this.text.length();
  }

  public String getFilter() {
      return this.filter;
  }
}