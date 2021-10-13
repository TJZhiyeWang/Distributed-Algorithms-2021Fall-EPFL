#!/bin/bash

# Change the current working directory to the location of the present file
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )" 

rm -f "$DIR"/bin/da_proc.jar
rm -rf "$DIR"/target
