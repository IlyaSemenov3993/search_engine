<h1 align="center">Search Engine</h1>

<h1>General info</h1>
The application was created according to the classic MVC structure.

<h1>Technologies</h1>
<ul>
    <li>Core - Java 11, Spring Boot 2</li>
    <li>Data - PostgreSQL, Liquibase, JDBC, JPA</li>
    <li>View - Javascript, HTML, CSS</li>
    <li>Test - Junit 4, Mockito</li>

</ul>

<h4></h4>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/877dd954d603839d3f5f5e67b2c24e080498f22b/assets/UntitledDiagram.drawio.png"></div>
<div>
    For the application to work correct, you need a Postgres database, and also register the data in the config
    file(application.properties, sites.properties ):
    <ul>
        <li>The path to the database</li>
        <li>Database user data (username, password)</li>
        <li>Sites that need to be indexed (main URL, some site name)</li>
    </ul>
</div>

<h1>About the project</h1>
I was given a ready-made frontend, my task was to write a backend. The functionality of the program can be divided into three parts:
<ul>
    <li>View general statistics about all sites and about each site individually</li>
    <li>Start or stop full indexing, indexing of a new site , indexing of a single page in the sites that are in the config file</li>
    <li>Search by search query on all sites or on a selected site</li>
</ul>
<h3>Dashboard</h3>
This tab opens by default. It displays general statistics for all sites, as well as detailed statistics and status
of the sites.
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/Dashboard2.png?raw=true" alt="Dashboard"></div>
<div>
    General statistics include:
    <ul>
        <li>The sites counter</li>
        <li>The pages counter</li>
        <li>The lemmas counter</li>
    </ul>
</div>
<div>
    Detailed statistics include:
    <ul>
        <li>Name of site - Set in the config</li>
        <li>Address of site - Set in the config</li>
        <li>Status - may be is "Indexing", "Indexed" and "Failed"</li>
        <li>Status time - the time of the last change in the status of the site</li>
        <li>Pages - the pages counter of the site</li>
        <li>Lemmas - the lemmas counter of the site</li>
        <li>Error - indexing error if there was one</li>
    </ul>
</div>
<h3>Management</h3>
This tab contains the search engine management tools ??? starting and stopping full indexing (reindexing),
as well as the ability to add (update) a separate page by link. Indexing of sites is performed independently
in different threads.
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/Management.png?raw=true"></div>
<h3>Search</h3>
This page is designed to perform a search on indexed sites of the search engine. 
It contains a search field, a drop-down list with a choice of the site to search for, 
and when you click on the "Find" button, the search results are displayed.
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/SearchEmpty.png?raw=true"></div>
<h3></h3>
<div>No matter what form the word is in the search query, the search engine will still find it.
    All links are active (at the time of indexing) and tabs open in a new window. Search queries are displayed in
    descending order of relevance. The relevance of the page is calculated during the search process.</div>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/Search1.png?raw=true"></div>
<h3></h3>
<div>If the search words are in the same sentence, the search engine will combine them into one snippet.</div>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/SearchOneResult.png?raw=true"></div>
<h3></h3>
<div>If there are more than ten search results, they are displayed in parts (ten results each),
    and the corresponding "Show more" button is displayed at the end of the page.</div>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/SearchShowMore.png?raw=true"></div>
<h3>Correct error responses</h3>
In case of exceptions, API methods return correct responses.
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/EmptyQuery.png?raw=true"></div>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/ErrorUrl.png?raw=true"></div>

<h1>Code Examples</h1>
This type of request/response when loading the start page(Dashboard)
<h3>Request</h3>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/Request.png?raw=true" width="400"></div>
<h3>Response</h3>
<div><img src="https://github.com/coollappsus/SearchEngine/blob/main/assets/Response.png?raw=true" width="400"></div>

<h1>Inspiration</h1>

<h1>Future scope</h1>
<ul>
    <li>
        Implement the output of search results in cases where not all lemmas are found.
        In this case, output an additional message like "Nothing was found for the ORIGINAL_QUERY.
        Corrected request: REQUEST".
    </li>
    <li>
        Authorization in the web interface.
    </li>
</ul>

<h1>Contact</h1>
Created by <a href="https://t.me/ilyusha2099"> Ilya Semyonov</a>
<h3></h3>
