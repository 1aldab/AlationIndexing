# Alation Indexing

Efficient Indexer and QueryServer on a large list of `<name, score>` pairs

April 15, 2017

## Overview

The project is to design and implement the following. Given a list of one million `<String name, int score>` pairs where names are valid Java variable names, write two programs and try to optimize their efficiency.
*	A `Construction Program` that produces a serializable data structure `D` (say `JSON` or `ProtocolBuffer`).
*	A `Query Server Program` that reads in serialized `D` and then accepts user queries such that for each query `s`, it responds with the top 10 names (ranked by score) that start with `s` or contain `‘_s’` (so for example, both “revenue” and “yearly_revenue” match the prefix “rev”). Query answering should run in sublinear time (in terms of the number of names in the input).

## Resources

In this project, the following open-source libraries are used:
*	gson: A Java serialization/deserialization library that can convert Java Objects into JSON and back. Available: https://github.com/google/gson
*	JUnit: A unit testing framework for Java. Available: https://mvnrepository.com/artifact/junit/junit

##	Design and Implementation

###	Tools and Technologies
I implemented this project in Java. I used Maven to manage the development lifecycle and IntelliJ as the IDE. For unit testing, I used JUnit. Also, I used java.util.logging extensively throughout my implementation to keep track of the program’s performance.

###	What and How?
My design is generally based on a prefix tree data structure. Particularly, I chose to use a standard `TreeMap` to store the keywords. A leaf in the tree contains a partially ordered set of at most 10 `<name, score>` pairs, where name is a string matching the keyword on the branch leading to it and score is the corresponding score for that name. The keywords are made by tokenizing names (made lowercase to allow case-insensitive search) using `“_”` as the delimiter. For example, the pair `“<June_Sales, 100>”` results in two keywords: `“june”` and `“sales”`. In the prefix tree, hence, we store the same pair under both `“june”` and `“sales”` branches. This design results in some redundancy but ensures efficient search time, because a query for either keyword would find the same pair in the tree in `O(k)` time where `k` is the length of the query.

The `Indexer` class in my project is the construction program that creates and maintains the prefix tree (named `namesTrie`). The constructor accepts the path to an external file. If the file is JSON, it deserializes the prefix tree from the JSON file. Otherwise, it treats the file as a comma separated text and attempts to build the prefix tree from its content. The method `toJson(String pathToJsonFile)` is to serialize the prefix tree to a JSON file as required by the project specification. This method and the deserializer method accepting the JSON file name both use the gson library for object serialization/deserialization.

The `QueryServer` class serves to (1) search the prefix tree for user generated queries, and (2) output the top matching names. The first stage is done through `getMatchingQueries(String prefix)` method. This method combines all lists of pairs having matching names with the input prefix. Accessing the right branch of the prefix tree is performed in `O(k)` time where `k` is the length of the prefix and populating all matching pairs is done in `O(m)` where m is the number of matching names. Since for each query we need to eventually return only the top ten matches, each leaf is set not to store more than ten candidates with the highest score.

As mentioned earlier, with the proposed design, a same name may be stored under different branches of the prefix tree. This can cause problem for certain queries. For example, the name `“Sales_Sep”` will be stored under both `“sales”` and `“sep”` branches. When querying `“s”`, both branches are examined and duplicate keys are retrieved. To eliminate such duplicates, I used a HashSet ensuring that pairs with the same name and score will not show up in the results more than once.

To efficiently obtain top ten names from the list of all matching pairs, I used a slightly modified version of the heap sort. For that, I created a min-heap and added pairs one by one. When the heap size reaches ten, following to the addition of every new pair, the min is also removed to make sure that the heap size remains fixed at ten. This keeps the maximum number of comparisons constant at all times and overall the time complexity of the sort remains `O(m)` where `m` is the number of matching pairs.

###	Alternatives

Before the above-mentioned design, I pursued a different approach that did not guarantee the sublinear time complexity requirement. In that design, I used a `TreeMap` but instead of storing tokens along the branches, I would store actual names. To enable search for queries containing `“_s”`, I also stored names backwards around the `“_”` delimiter. That would work fine finding names beginning with `“s”` or ending with `“_s”`. However, if a name contained `“_s”` in the middle, this approach would fail to match that. One way to address that issue was adding all permutations of tokens for each name to the tree, but that would cost exponential space as the number of tokens in the name increased. Another solution was to store names as is and for every query, search all keys using a regex. However, regex search is linear with respect to the input size and does not meet the project requirement of sublinear time. Caching the results of similar queries would arguably improve the average search time but at the cost of additional space.

## Deployment Plan

Maven can be used for automation of test, packaging, deployment and almost every other phase of the development lifecycle. Packaging, for instance, is as easy as running `mvn package` in the project home to make the project JAR file.

###	Using the API

To configure and use the proposed API, it is assumed that a CSV file exists containing `<name, score>` pairs. An instance of `Indexer` class can be created as follows using the path to the CSV file as input:
`Indexer indexer = new Indexer(csvFileName);`

Next, a `QueryServer` can be made using the `indexer` instance as follows:
`QueryServer qs = new QueryServer(indexer)`;
 
Then, the `QueryServer` instance is ready to be queried according to the following syntax that returns the top 10 matching names sorted by decreasing scores:
`String[] topMatches = qs.getTopMatches("sample_query", 10);`

###	Monitoring

According to the project description, query response time is a critical performance metric. To monitor the performance of the proposed program, we can profile different components (e.g., object serialization, deserialization, building the indexer from CSV file, query search, etc.) of the program to find potential bottlenecks. Also, query response times for different input sizes can be measured and tracked to ensure that the program satisfies the sublinear time requirement.

###	Logging

I used `java.util.logging` extensively throughout my implementation to keep track of the program flow as well as the internal behavior of different components. The verbosity of the logging modules can be set through `logging.properties`. A sample of this file is provided within the project directory. When running the program, this file can be passed to the VM using option `-Djava.util.logging.config.file=/path/to/logging.properties`

## Test Plan

For unit testing, I used JUnit. All test classes are under `src/test/java/alation`. Class `AllTests` is in fact a test suite that encompasses two other test classes: `IndexerTest` and `QueryServerTest`.

1.	Class `IndexerTest`

This class runs two test methods. One is to build an indexer and its corresponding prefix tree from a CSV file and making sure that they are not null. The other tests serialization and deserialization of the prefix tree to JSON and making sure that the tree after deserialization is indeed the same tree before serialization.

2.	Class `QueryServerTest`

This class runs seven parameterized tests on the `QueryServer`:
   - Searching for keyword `“Sal”`: Should match both `“Sales”` and `“Salary”`.
   - Searching for keyword `“Sales”`: Should match all occurrences of `“Sales”` only.
   - Searching for capitalized keyword `“REVENUE”`: Should match all instances of `“Revenue”` regardless of the case.
   - Searching for lower-case query `“rev”`: Should match all instances of `“Revenue”`.
   - Searching for single character query `“s”`: Should match names containing `“Sales”`, `“Salary”` and `“September”`. Also, `“Sep_Salary”` should appear only once, although it matches two separate branches in the tree.
   - Searching for non-existent query `“xyz”`: Should return an empty list.
   -	Searching for empty query `“”`: Should return top scorer names.
To run test cases, in the project home directory run `mvn test`.
