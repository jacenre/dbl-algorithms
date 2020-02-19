# DBL Algorithms Group 2

A group project for the DBL Algorithms course at Eindhoven University of Technology.

## Requirements
- Java SDK 8
- JUnit 5.6 for testing

## Terminology
| Term  |  Description |
|---|---|
| Rectangle |  The shapes that are being packed in the PackingSolver. |
| Box | The shape in which all rectangles are placed and packed. 
| | Might or might not have a fixed height. |
| Strip | Box with a fixed height. |
| Bin  | Stores the Parameters and additional satellite data to serve a test case. |

# Example inputs
    container height: fixed 22
    rotations allowed: yes
    number of rectangles: 6
    12 8
    10 9
    8 12
    16 3
    4 16
    10 6
    
    container height: free
    rotations allowed: no
    number of rectangles: 6
    12 8
    10 9
    8 12
    16 3
    4 16
    10 6