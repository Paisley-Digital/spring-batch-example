## Spring Batch Example

This a simple program which reads from a CSV file and saves the data in 
a database.
##  
Regarding the faultTolerant behavior, it depends on business requirement and needs more knowledge about the domain. Should we retry, skip invalid lines, log them to file/database, fail the whole file or just skip them, etc.
##
We skip invalid lines and log an error.
For Object validation I used JSR 303 Bean Validation Annotations and skip inserting invalid record and logs them as error.

