To Do
=====

[![Build Status](https://travis-ci.org/jhu-oose/todo.svg?branch=master)](https://travis-ci.org/jhu-oose/todo)

A [To Do sample application](https://oose-to-do.herokuapp.com). The implementation is divided in two parts: server and client. They communicate via the **To Do API**, specified in the Postman **Collection** at `docs/To Do API Specification.postman_collection.json`. Import the collection in Postman to read the API documentation.

Run a local instance by importing this project in IntelliJ as a Maven project and starting `src/main/java/edu/jhu/cs/pl/to_do/Server.java`. In Postman, request the local server by switching to the `Development` environment as defined in `docs/Development.postman_environment.json`.

We have made a video on how you can set up and run this app in the IntelliJ IDE; pick your format, [.m4v](http://pl.cs.jhu.edu/oose/movies/todo.m4v) or [.mov](http://pl.cs.jhu.edu/oose/movies/todo.mov).

Contents
--------

- `docs`: The Postman specification and environment.
- `src/main/java`: The code for the server.
- `src/main/resources`: The code for the client.
- `src/test`: Unit tests with JUnit.
- `.travis.yml`: Travis CI configuration.
- `pom.xml`: Maven configuration.
- `Procfile` and `system.properties`: Heroku configuration.
