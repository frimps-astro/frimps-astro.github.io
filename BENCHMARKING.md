---
layout: page
title: Benchmarking Analysis
permalink: /assignment/benchmarking
---
# Benchmarking Discussion
The benchmarking environment was as follows:
```
Java version: openjdk 19
Benchmark version: openjdk jmh 1.35

System Memory: 12gig
Cache: 256kb L1 and 2mb L2
CPU: AMD Quadcore, 2.0gHz x2, 2.1gHz and 1.2gHz
OS: Ubuntu, Linux
Kernel: 5.15.0-52-generic
Architecture: 64bit
```

The order of execution of benchmarked methods was based on the methods names in alphabetical order. The goal was to execute the insertion method first to have some data at hand for search and sort but I believe that was not the case per the execution order.

## Results Analysis
The time unit used for the benchmarking was `seconds` and all methods were executed in `10 iterations`
- ### **INSERT**

| Datastructure        | Throughput           | Average Speed  |
| ------------- |:-------------:| -----:|
| Hashset     | 1.338 | 0.861 |
| LinkedHashset      | 1.299      |   0.894 |
| Treeset | 0.336      |    3.553 |

***Analysis:*** 

Hashset has higher throughput (the number of insertions per unit time) for inserting data because it maintains no particular order of its items i.e. items are inserted randomly and they are kept that way. 

LinkedHashset maintains a linkedlist to internally maintain the insertion order of items hence there is a bit of overhead which reduces its throughput and also requiring a bit of additional time as compared to the hashset.

Treeset sorts its items after evertyhing insertion operation hence a greater overhead reducing its throughput and average speed.

- ### **SEARCH - CONTAINS**

| Datastructure        | Throughput(ops/s)           | Average Speed(s/op)  |
| ------------- |:-------------:| -----:|
| Hashset     | 47301736.005 | ≈ 10⁻⁸ |
| LinkedHashset      | 47261893.501      |   ≈ 10⁻⁷ |
| Treeset | 43910040.127      |    ≈ 10⁻⁸ |

***Analysis:***

An item could be located anywhere in a hashset when they are not ordered hence searching for an item takes less time and has higher throughput.

LinkedHashset takes relatively longer time compared to the hashset but has greater throughput than the Treeset because its elements maintain an insertion order where as a Treeset needs to traverse through all elements in the case when an item being searched for is deep down the tree.

- ### **SORT**

| Datastructure        | Throughput           | Average Speed  |
| ------------- |:-------------:| -----:|
| Hashset     | 19837891.449 | ≈ 10⁻⁷ |
| LinkedHashset      | 21117581.974      |   ≈ 10⁻⁷ |
| Treeset | 20360220.989      |    ≈ 10⁻⁷ |


***Analysis:***

Hashset has a less throughput for sorting due to the fact that it has its items randomly disbruted.

LinkedHashset has a greater throughput because its items are linked to each other hence easily to sort.

Treeset does have a high throughput because it constantly maintains an order of its items. In cases where, we need to sort in a particular order (ascending or descending), the list of items would only have to be reversed.

### Conclusion on Results
From the results it is evident that the performance of these sets are in the order Hashet > LinkedHashset > Treeset.

But the choice for any of these datastructures depends on what the goal is: Hashset if you need no order of items, LinkedHashset if you want to maintain the order of insertion and Tree if you want items in a particular order.

In terms of memory, Hashset requires less memory because it only uses a hashmap to stores its items while a LinkedHashset uses hashmap and also maintains insertion order hence more memory required. Tree uses more memory than the other two because it uses the treemap and also an ordering comparator.