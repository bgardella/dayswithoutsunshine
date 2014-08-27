The Uber Challenge -- SF Movies
===================
This is a Full Stack implementation of the web coding challenge 
found: [here](https://github.com/uber/coding-challenge-tools)

The finished application can be found here: [http://54.241.0.47/](http://54.241.0.47/)

Technologies Used
-----------------
* Java 1.7
* Spring Framework (MVC, Dependency Injection, etc)
* ElasticSearch
* JQuery
* jsonp
* sass
* jsp
* Google Maps API

Technologies NOT Used But Probably Should Have
----------------------------------------------
* Maven
* Backbone.js

Why Did I Build It This Way? 
----------------------------
I have extensive experience in everything used here with the exception 
of ElasticSearch.  I've been using SOLR for the last few years, and I 
kept hearing about ElasticSearch.  This project was the perfect time to 
break it out and see for myself.  

The Java/Spring/JQuery stack is something I've been using for many years and
I've built a number of personal projects on this stack. I made it clear in 
the Java packaging what is new and what is boilerplate from years past.  The 
javascript/css is somewhat less clear.  

Tradeoffs
---------
I underestimated the quality of the data.  In order to make it useful, I had 
to spend some time getting it into ElasticSearch in a usable way.  Location data
had to be harvested without proper addresses.  Google rescued me there. I would
like to have spend more time thinking how to make the tool more fun.  It's not 
the best UI to induce discovery.  I did try a few things, but I knew from 
experience how much time that can take to get right.  I had to keep it simple.

Deployment
----------
* Tomcat 7
* AWS -- EC2 -- Single Spot Instance 

What Is Missing?
----------------
* N-Gram support for better autocomplete
* Scheduled Job Process to periodically re-run the indexer to pull new data from the 
SF-GOV website
* Mobile/Tablet presentation logic 

What's Left To Do?
------------------
* junit testing
* intern testing


