@echo off
title Shipping Label Creator
echo Welcome to the shipping label creator
if exist "shipping-label-input.csv" (
    echo Processing file 'shipping-label-input.csv'
    java -jar shipping-label-creator-csv-0.3.jar -i "shipping-label-input.csv"
    if exist "a.tex" (
        echo Converting to pdf
        pdflatex --quiet a.tex
        echo PDF file is present as a.pdf
        del a.aux a.log
    ) else (
        echo Something went wrong with generating the tex
    )
) else (
    echo Input file 'shipping-label-input.csv' is missing
)
pause
