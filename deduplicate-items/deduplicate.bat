@echo off
title Deduplicate item
echo Welcome to the item deduplicator
if exist "deduplicate-input.csv" (
    echo Processing file 'deduplicate-input.csv'
    java -jar deduplicate-items-0.3.jar -i "deduplicate-input.csv" -o "deduplicate-output.csv"
) else (
    echo Input file 'deduplicate-input.csv' is missing
)
pause
