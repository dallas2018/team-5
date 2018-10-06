import requests
from bs4 import BeautifulSoup
import statistics
import sys

google_shopping_url = 'https://www.google.com/search?hl=en&tbm=shop&q={}'

def process_prices(prices):
    if len(prices) == 0:
        return 0
    else:
        return statistics.median(prices)

def scrape_prices(request):
    """Responds to any HTTP request.
        Args:
            request (flask.Request): HTTP request object.
        Returns:
            The response text or any set of values that can be turned into a
            Response object using
            `make_response <http://flask.pocoo.org/docs/0.12/api/#flask.Flask.make_response>`.
        """
    request_json = request.get_json()

    terms = request_json['terms']

    return get_price_for_terms(terms)


def get_price_for_terms(terms):
    # Format terms with plusses
    formatted_terms = ''.join(a + '+' for a in terms)

    # spoof a browser
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36'}

    # Create URL to request
    url = google_shopping_url.format(formatted_terms)

    # Create request
    request = requests.get(url, headers=headers)

    # Put shopping.html page into beautiful soup
    soup = BeautifulSoup(request.text, 'html.parser')

    # Find all prices on page
    prices = soup.findAll("span", {"class": "O8U6h"})

    # Format prices and remove $
    formatted_prices = [price.text.replace('$', '').replace(',', '') for price in prices]

    floats_ = [float(a) for a in formatted_prices]

    # Return formatted prices
    return process_prices(floats_)

if __name__ == '__main__':
    print(get_price_for_terms(sys.argv[1:]))
