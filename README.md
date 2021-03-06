The Uber Challenge -- SF Movies
===================
This is a Full Stack implementation of the web coding challenge 
found: [here](https://github.com/uber/coding-challenge-tools)

The finished application can be found here: [http://54.241.0.47/](http://54.241.0.47/)

Who Am I?
---------
* [LinkedIN](https://www.linkedin.com/in/bengardella)
* [github](https://github.com/bgardella)
* [assembla](https://www.assembla.com/profile/bgardella)


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
the Java packaging (org.gardella vs. phor.uber) what is new and what is boilerplate 
from years past.  The javascript/css/jsp is almost entirely new.  

Tradeoffs
---------
I underestimated the quality of the data.  In order to make it useful, I had 
to spend some time getting it into ElasticSearch in a usable way.  Location data
had to be harvested without proper addresses.  Google rescued me there. I would
like to have spend more time thinking how to make the tool more fun.  It's not 
the best UI to inspire discovery.  I did try a few things, but I knew from 
experience how much time that can take to get right.  I had to keep it simple.

Deployment
----------
* Tomcat 7
* AWS -- EC2 -- Single Spot Instance 

Testing
--------
* Junit test harness can be run from ant:  $ant clean test
* Intern JS test can be run from /test/run-local.js

What Is Missing?
----------------
* N-Gram support for better autocomplete
* Scheduled Job Process to periodically re-run the indexer to pull new data from the 
SF-GOV website
* Mobile/Tablet presentation logic 
* Integration with IMDB and Google Image Search, because...why not?
* Promises.  Because Promises are cool.


