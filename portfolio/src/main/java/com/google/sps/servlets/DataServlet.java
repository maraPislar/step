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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/** Servlet that encapsulates comments. */
@WebServlet("/data")
public final class DataServlet extends HttpServlet {
  @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Query query = new Query("Comment");

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);

      List<Comment> comments = new ArrayList<>();
      for (Entity entity : results.asIterable()) {
        long id = entity.getKey().getId();
        String text = (String) entity.getProperty("text");
        String name = (String) entity.getProperty("name");
        Comment comment = new Comment(id, text, name);
        comments.add(comment);
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

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("text", userComment);
      commentEntity.setProperty("name", userName);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      // Redirect back to the HTML page.
      response.sendRedirect("/comment.html");
    }

    public class Comment {
      public long id;
      public String text;
      public String name;

      public Comment(long id, String text, String name) {
        this.id = id;
        this.text = text;
        this.name = name;
      }
    }
}