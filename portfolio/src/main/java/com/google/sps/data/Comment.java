/**
* A comment has an id, text and an author. 
*/

package com.google.sps.data;

public final class Comment {
  public final long id;
  public final String text;
  public final String author;

  public Comment(long id, String text, String author) {
    this.id = id;
    this.text = text;
    this.author = author;
  }
}