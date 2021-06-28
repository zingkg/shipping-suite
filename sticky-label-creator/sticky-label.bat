@echo off
title Sticky Label Creator
echo Welcome to the sticky label creator
if exist "sticky-label-input.csv" (
    echo Processing file 'sticky-label-input.csv'
    java -jar sticky-label-creator-0.4.jar -i "sticky-label-input.csv"
    if exist "a.tex" (
        echo Converting to pdf
        pdflatex --quiet a.tex
        echo PDF file is present as a.pdf
        del a.aux a.log
    ) else (
        echo Something went wrong with generating the tex
    )
) else (
    echo Input file 'sticky-label-input.csv' is missing
)
pause
