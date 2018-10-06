To use the label to price script run

python get-price.py {labels}

labels is the list of labels seperated by spaces

for example

python get-price.py computer laptop grey

prints out

358.88

which is the median price for that search.

Possibly we could use a more complex algorithm than median, like weighting by search result number.
