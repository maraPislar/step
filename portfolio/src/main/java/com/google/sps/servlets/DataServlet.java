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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PreparedQuery results = fetch(); 
    List<Comment> comments = toInternal(results);
    String filter = getSortingCriterion(request);
    int quantity = getNumberComment(request);
    sort(comments, filter); 

    if(quantity > comments.size()) {
        quantity = comments.size();
    }
    
    List<Comment> limitedComments = new ArrayList<Comment>();
    for (int i = 0; i < quantity; i ++) {
      limitedComments.add(comments.get(i));
    }

    String json = toJson(limitedComments);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    addToDatastore(request);

    // Redirect back to the HTML page.
    response.sendRedirect("/comment.html");
  }

  /* Prepare the query */
  public static PreparedQuery fetch() {
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    return datastore.prepare(query);
  }

  /* Construct the array of Comment objects to manipulate it after */
  public static List<Comment> toInternal(PreparedQuery results) {
    List<Comment> comments = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      String author = (String) entity.getProperty("author");
      String mood = (String) entity.getProperty("mood");
      long timestamp = (long) entity.getProperty("timestamp");
      String userEmail = (String) entity.getProperty("email");

      Comment comment = new Comment(id, text, author, mood, timestamp, userEmail);
      comments.add(comment);
    }

    return comments;
  }

  /* Create filters for timestap and text of comment */
  public static Comparator timestampFilter() {
    Comparator<Comment> byTimestamp = Comparator.comparingLong(Comment::getTimestamp);
    return byTimestamp;
  }

  public static Comparator textFilter() {
    Comparator<Comment> byLength = Comparator.comparingInt(Comment::getTextLength);
    return byLength;
  }

  /* Filter the comments => sort by timestamp or text */
  public static void sort(List<Comment> comments, String userFilter) {
    if ("newest".equals(userFilter)) {
      Collections.sort(comments, timestampFilter().reversed());
    } else if ("oldest".equals(userFilter)) {
      Collections.sort(comments, timestampFilter());
    } else if ("longest".equals(userFilter)) {
      Collections.sort(comments, textFilter().reversed());
    }
  }

  /* Convert the filtered comments to json */
  public static String toJson(List<Comment> comments) {
    Gson gson = new Gson();
    return gson.toJson(comments);
  }

  /* Get the input from the form, create entity and add to datastore*/
  public static void addToDatastore(HttpServletRequest request) {
    String userComment = request.getParameter("user-comment");
    String userName = request.getParameter("user-name");
    String userMood = request.getParameter("mood");
    long timestamp = System.currentTimeMillis();

    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("text", userComment);
    commentEntity.setProperty("author", userName);
    commentEntity.setProperty("mood", userMood);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("email", userEmail);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  /* Get the requested filter and also look for input errors */
  public static String getSortingCriterion(HttpServletRequest request) {
    String filter = request.getParameter("filter");

    if ("newest".equals(filter) || "oldest".equals(filter) || "longest".equals(filter)) {
      return filter;
    } else {
      System.err.println("This filter is not valid or null: " + filter);
      return "newest";
    }
  }

  /* Get the number of comments to show and check the input */
  public static int getNumberComment(HttpServletRequest request) {
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