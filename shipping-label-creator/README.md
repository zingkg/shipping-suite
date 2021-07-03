# shipping-label-creator
A shipping label creator generating a LaTeX file to be used to generate a PDF programmatically.

Deprecated, please utilize shipping-label-creator-csv

## Installation
Shipping Label Creator requires LaTeX, and Java 7 or higher to run. To build, gradle is required.

Assemble the JAR through gradle by: `gradle assemble`. A shipping-label-creator-xx.jar will appear
in the build/libs directory. This JAR is the executable, a `Settings.ini` file should accompany this
JAR wherever it goes. Below is an example of Settings.ini.

Settings.ini:
```ini
[Source]
name = Bob Bobber
address = 123 Fake Street
city = Bobberity
state = Bobate
zip_code = 12345

[Latex]
pdf_latex_path = samplepath/latex/pdflatex
should_add_entry_line = false
```

`pdf_latex_path` should point to wherever the pdflatex executable is.

## Usage
![alt text](https://cloud.githubusercontent.com/assets/20845097/19744908/4fc18fe8-9b84-11e6-9ca8-4dbda815d2d4.jpg "Main Window")

The destination section is the destination of all of the items. The item section is meant to be
filled in with details to describe the item. The boxes is mandatory. This describes how many boxes
that the item requires. This must be an integer. The Add Item box will add it to the list of Order
Items. This can be loaded with a Load Item. Clear Item will remove all fields in the Item section.
Create PDF will create a PDF given that `pdf_latex_path` is properly configured. Remove will remove
a item in the Order Items list. The text size for the PDF can be modified with the spinner.

## License
MIT License see [LICENSE](https://github.com/zingkg/shipping-label-creator/blob/master/LICENSE).
