# Word Count

## Development Environment

### Prerequisites
- Java 11
- mysql 8.0.13

### Application Properties

Default application properties:
```
export DB_PASSWORD=1@mr00t
export DB_USERNAME=root
export DB_SCHEMA=wordcount
export DB_CONNECTION_STRING=jdbc:mysql://localhost:3306?serverTimezone=UTC
export USERS_CONNECTION_STRING=jdbc:mysql://localhost:3306/wordcount?serverTimezone=UTC
```

### Database Schema

Create (or recreate) `wordcount` DB schema with:

```bash
./gradlew flywayClean flywayMigrate
```

`DB_` environment params will need to be available wherever you are running this command.



### Running migrations

Flyway gradle plugin handles db migrations. Spring boot will run migrations on startup.

- migration scripts location ```src/main/resources/db/migration```
- to migrate db run ``` ./gradlew flywayMigrate -i ```
- to clean db run ``` ./gradlew flywayClean ```
- to clean and migrate ``` ./gradlew flywayClean flywayMigrate ```
- to repair meta data run ``` ./gradlew flywayRepair ```
- to check current db migrate info run ``` ./gradlew flywayInfo ```
- https://flywaydb.org/documentation/


### Running the build

```bash
./gradlew clean build 
```



### Running the build with Jacoco Test Coverage

```bash
./gradlew clean build jacocoTestReport jacocoTestCoverageVerification
```


### Running the app
1. Run backend:
- Via IDE : Run WordCountApplication
- Via terminal : Navigate to the root directory of the project, and use:
```bash
 ./gradlew bootRun
```

2. Access the word counting API ```api/demo/word-count/``` via a ```POST``` request.
   - Example:
   - POST Request: ``` http://localhost:8080/api/demo/word-count/ ```
   - Authorization (Basic Auth) :
   ```
		username = <See Authentication section below> ,
		password = <See Authentication section below>
   ```
   - JSON Request Body:
   ```
        {
            "source" : <TXT filepath> ```
            "k_value" : <Integer value for most frequent words to search> ```
         } 
   ```


## NOTES

## Word Counting Request
``` source ``` must contain a valid and existing filepath. It can either be URL filepath or from a local directory.
Depending on which was supplied, a different file downloader will be used.

``` k_value ``` must be a whole number and greater than 0.
If a decimal number is supplied, only the integer part is considered. Example: 2.9 will be considered as 2


## Word Counting 
- Counting is case-insensitive
- A word starts with alphabet or number. A word starting with underscore is not a word.

- Logic:
	| Raw word | Processing  |
	|----------|----------|
	|``` abc123        ``` | word, will be processed as: ``` abc123 ```|
	|``` abc123abc     ``` | word, will be processed as: ``` abc123abc ```|
	|``` 123abc        ``` | word, will be processed as: ``` 123abc ```|
	|``` abc           ``` | word, will be processed as: ``` abc ```|
	|``` abc,          ``` | word, will be processed as: ``` abc ```|
	|``` ,abc,         ``` | word, will be processed as: ``` abc ```|
	|``` aBc           ``` | word, will be processed as: ``` abc ```|
	|``` _abc          ``` | not a word |

- Other scenarios:	
	| Raw word | Processing  |
	|----------|----------|
	|``` !@#abc&*^     ``` | word, will be processed as: ``` abc ```|
	|``` !#$___@abc&*^ ``` | word, will be processed as: ``` abc ```|
 

- Result:
	| Word | Count  |
	|----------|----------|
	|``` abc ``` | 6 | 
	|``` abc123 ``` | 1|
	|``` abc123abc ``` | 1 |
	|``` 123abc ``` | 1|

- Results are displayed in descending order of count. 
For words with the same count, they are displayed with no definite order. 


## Word Counters
In my past work experiences, I've dealt mostly with the use of libraries. For this counting app, I used Java's libraries.  


### Word Counters - Concurrent
The first counter ```ConcurrentFileBufferWordCounter```:
- Opens the file, reads the contents per line, and concurrently counts the words per line.
- Results are then merged, arranged in descending count order, and from these, the top k most frequent words will be retrieved

- This counter, however, will crash when processing a 1 gig file.


### Word Counters - Concurrent Chunked
The second counter ```ConcurrentMemoryMappedChunkingWordCounter```:
1. Opens the file and saves all the contents in memory
2. Data in memory is then chunked / divided into bytes
  ```
  Current value: 1048576 bytes.
  Bytes value is configurable in application.properities :: api.word.count.chunked.buffer.bytes.size
  During chunking, a word's maximum length is 100 (default).
  This is also configurable in application.properities :: api.word.count.chunked.word.maxLength
  ```
3. Once chunking is done, these chunked data are then processed concurrently
4. Results are then merged, arranged in descending count order
5. From that, the top k most frequent words will be retrieved

- This counter can process a 1 gig file for 3-4 minutes.
- This counter is limited to process a filesize below Integer.MAX_VALUE (2,147,483,647) bytes.


- The counter to use can be configured in ``` application.properities --> api.word.count.mode```
  ```api.word.count.mode``` = ```CONCURRENT``` (uses the 1st counter)
  ```api.word.count.mode``` = ```CONCURRENT_CHUNKED``` (uses the 2nd counter)
  
- When none is supplied,  ```CONCURRENT_CHUNKED``` is the default counter

### Word Counters - Future updates
- <i> Support for new counters (eg: use of data algorithms) can easily be applied with minimal change to existing code logic. </i>


## Caching
- Results of data with same request ( ``` same source and k_value  ```) will be saved / cached for ```30 minutes``` (default value).
- The application uses ```Ehcache``` for this caching functionality. The caching expiry can be configured in ``` ehcache.xml --> expiry ```)
- Whenever a data will be cached, this info log will be shown: ``` [Cache Event] CREATED ```


## Authentication
The application uses ```Spring Security's JDBC Authentication```. 
These are the default users that can access the API:

| username |  password |  roles | 
|----------|----------|----------|
| ``` tester ``` |  ``` w1nt3r101! ``` |  ROLE_USER | 
| ``` admin  ``` |  ``` 1@M@dm1n   ``` |  ROLE_USER and ROLE_ADMIN | 

- A user role (ROLE_USER) can access ``` ~/api/demo/word-count/ ```
- An admin role (ROLE_ADMIN) can access API's from a user's role and other future API's in ``` /api/demo/** ```


## Swagger Documentation
- Swagger can be accessed through any of the link below.
```
http://localhost:8080/swagger
http://localhost:8080/swagger-ui/index.html
```
- If prompted with an authentication, use the authentication details provided above.
