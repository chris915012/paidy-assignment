# Forex-mtl
Simple scala program for Paidy assignment.

# Overview

## Requirements
> The service returns an exchange rate when provided with 2 supported currencies
> The rate should not be older than 5 minutes
> The service should support at least 10,000 successful requests per day with 1 API token

## How to run
* Run One-Frame API server according to this page. https://hub.docker.com/r/paidyinc/one-frame
* Build the project `forex-mtl` with sbt.
* Test with calling URL `[GET] http://localhost:5000/rates?from=USD&to=JPY`

# Breakdown

## Implementation
* Created `live` interpreter to get a price from One-Frame API
* Set error message when it fails to retrieve the data on `live` OneFrameClient object.
> Adapted the existing source code from Errors object.
* Set cache on retrieving One-Frame API to handle concurrency of the cache. 
> Created Cache object using with HashMap by myself. 
> Created `get`, `put`, `remove` method.
> (Personally, I don't like to adapt a cache on a finance information, but I had no chance because of the limitation of function calls on One-Frame API.)

# Further Improvements 

## Scala-styled code
As I have no experience using scala program before taking this assignment, the source code that I wrote could look weird on scala-styled.

## Testing
I believe writing thorough unit tests can help ensure that your code is working as expected and can catch regressions when making changes.

## Modulization & Refactoring
Optimizing the performance of your code can make it run faster and use fewer resources. This may involve optimizing algorithm complexity, minimizing unnecessary computation, or using efficient data structures.

## Cache
I implemented the cache object by myself, but it will be more benefitial if I adapt open source likee Guava or Caffeine.
