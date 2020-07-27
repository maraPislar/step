// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that encapsulates comments. */
@WebServlet("/data")
public final class DataServlet extends HttpServlet {
  String userFilter;
  int commentNumber;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      String author = (String) entity.getProperty("author");
      String mood = (String) entity.getProperty("mood");
      Long timestamp = (Long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, text, author, mood, timestamp);
      comments.add(comment);
    }

    /* Create filters for sorting the comments */
    Comparator<Comment> compareByTimestamp = (Comment c1, Comment c2) -> 
                            c1.getTimestamp().compareTo(c2.getTimestamp());

    Comparator<Comment> compareByText = (Comment c1, Comment c2) -> 
                            c1.getTextLength().compareTo(c2.getTextLength());

    /* Sort the comments by the filter selected by the user */
    if ("newest".equals(userFilter)) {
      Collections.sort(comments, compareByTimestamp.reversed());
    } else if ("oldest".equals(userFilter)) {
      Collections.sort(comments, compareByTimestamp);
    } else if ("longest".equals(userFilter)) {
      Collections.sort(comments, compareByText.reversed());
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String userComment = request.getParameter("user-comment");
    String userName = request.getParameter("user-name");
    String userMood = request.getParameter("mood");
    Long timestamp = System.currentTimeMillis();

    userFilter = getFilterChoice(request);
    commentNumber = getNumberComment(request);
    if (commentNumber == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between 0 and 10.");
      return;
    }

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("text", userComment);
    commentEntity.setProperty("author", userName);
    commentEntity.setProperty("mood", userMood);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/comment.html");
  }

  public String getFilterChoice(HttpServletRequest request) {
    String filter = request.getParameter("filter");

    if ("newest".equals(filter) || "oldest".equals(filter) || "longest".equals(filter)) {
      return filter;
    } else {
      System.err.println("This filter is not valid: " + filter);
      return "newest";
    }
  }

  /* Get the number of comments to show and check the input */
  public int getNumberComment(HttpServletRequest request) {
    String quantityString = request.getParameter("quantity");

    int quantity;
    try {
      quantity = Integer.parseInt(quantityString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + quantityString);
      return -1;
    }

    if (quantity < 0 || quantity > 10) {
      System.err.println("User choice is out of range: " + quantityString);
      return -1;
    }

    return quantity;
  }
}