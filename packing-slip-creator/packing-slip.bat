@echo off
title Packing Slip Creator
echo Welcome to the packing slip creator
if exist "packing-slip-input.csv" (
    echo Processing file 'packing-slip-input.csv'
    java -jar packing-slip-creator-0.8.jar -i "packing-slip-input.csv"
    if exist "a.tex" (
        echo Converting to pdf
        pdflatex --quiet a.tex
        echo PDF file is present as a.pdf
        del a.aux a.log
    ) else (
        echo Something went wrong with generating the tex
    )
) else (
    echo Input file 'packing-slip-input.csv' is missing
)
pause
