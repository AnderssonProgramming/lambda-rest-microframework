/**
 * Lambda REST Microframework - Client-side JavaScript
 * Handles API demo interactions on the index page.
 */

/**
 * Calls the /App/hello endpoint with the name from the input field.
 */
function callHelloApi() {
    const name = document.getElementById('nameInput').value || 'World';
    const url = '/App/hello?name=' + encodeURIComponent(name);
    callApi(url);
}

/**
 * Makes a fetch request to the specified API endpoint and displays the result.
 * @param {string} url - The API endpoint URL to call
 */
function callApi(url) {
    const resultBox = document.getElementById('result');
    resultBox.innerHTML = '<p style="color: #f39c12;">Loading...</p>';

    fetch(url)
        .then(function(response) {
            return response.text().then(function(text) {
                return { status: response.status, body: text };
            });
        })
        .then(function(data) {
            resultBox.innerHTML =
                '<p><strong>URL:</strong> <code>' + url + '</code></p>' +
                '<p><strong>Status:</strong> <span class="success">' + data.status + ' OK</span></p>' +
                '<p><strong>Response:</strong></p>' +
                '<pre class="success">' + escapeHtml(data.body) + '</pre>';
        })
        .catch(function(error) {
            resultBox.innerHTML =
                '<p><strong>URL:</strong> <code>' + url + '</code></p>' +
                '<p class="error"><strong>Error:</strong> ' + error.message + '</p>';
        });
}

/**
 * Escapes HTML special characters to prevent XSS.
 * @param {string} text - The text to escape
 * @returns {string} The escaped text
 */
function escapeHtml(text) {
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
}
