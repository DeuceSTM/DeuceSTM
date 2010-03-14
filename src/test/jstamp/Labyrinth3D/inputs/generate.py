#!/bin/env python

import random
import sys

if len(sys.argv) != 5:
    print "Usage: ./generate.py x y z n"
    sys.exit(1)

x = int(sys.argv[1])
y = int(sys.argv[2])
z = int(sys.argv[3])
numPath = int(sys.argv[4])

random.seed(0)

print "# Dimensions (x, y, z)"
print "d  %i %i %i" % (x, y, z)
print ""
print "# Paths: Sources (x, y, z) -> Destinations (x, y, z)"
for i in range(numPath):
    src = (random.randint(0, x-1), random.randint(0, y-1), random.randint(0, z-1))
    while 1:
        dst = (random.randint(0, x-1), random.randint(0, y-1), random.randint(0, z-1))
        if dst != src:
            break;
    print "p   %3i %3i %1i   %3i %3i %1i" % (src[0], src[1], src[2], dst[0], dst[1], dst[2])
