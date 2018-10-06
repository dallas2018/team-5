require('dotenv').load();
const express = require('express');
const app = express();
const port = process.env.PORT || 3000;
const admin = require('firebase-admin');
const vision = require('@google-cloud/vision');
var request = require('request');
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://jpmc4g-18.firebaseio.com",
    storageBucket: "jpmc4g-18.appspot.com"
});

const db = admin.firestore();
db.settings({ timestampsInSnapshots: true });

app.get('/', function (req, res) {
    res.send('Hello World!')
});

// Returning all the documents in the listings collection
app.get('/listings/all', function (req, res) {
    var listings = {};
    db.collection('listings').get()
        .then((snapshot) => {
            snapshot.forEach((doc) => {
                listings[doc.id] = doc.data();
            });
            console.log('/listings/all requested')
            res.json(listings);
        })
        .catch((err) => {
            console.log('Error getting documents', err);
            res.send('{}');
        });
});

app.post('/vision', function (req, res) {
    var response = new Object();
    response.tags = [];
    var terms = [];
    console.log(req.body['fileName']);
    const fileName = "gs://jpmc4g-18.appspot.com/" + req.body['fileName'];
    console.log(fileName);

    const client = new vision.ImageAnnotatorClient();

    const labelPromise = client
        .labelDetection(fileName)
        .then(results => {
            const labels = results[0].labelAnnotations;
            console.log('Labels:');
            labels.forEach(label => {
                console.log(label.description);
                response.tags.push(label.description);
            });

            // Getting the first match for title and rest for tags
            terms.push(labels[0].description);
            response.title = labels[0].description;
            response.tags.shift();
        })
        .catch(err => {
            console.error('ERROR:', err);
        });

    const logoPromise = client
        .logoDetection(fileName)
        .then(results => {
            const logos = results[0].logoAnnotations;
            console.log('Logos:');
            logos.forEach(logo => console.log(logo));
        })
        .catch(err => {
            console.error('ERROR:', err);
        });

    const webPromise = client
        .webDetection(fileName)
        .then(results => {
            const webDetection = results[0].webDetection;

            if (webDetection.fullMatchingImages.length) {
                console.log(
                    `Full matches found: ${webDetection.fullMatchingImages.length}`
                );
                webDetection.fullMatchingImages.forEach(image => {
                    console.log(`  URL: ${image.url}`);
                    console.log(`  Score: ${image.score}`);
                });
            }

            if (webDetection.partialMatchingImages.length) {
                console.log(
                    `Partial matches found: ${webDetection.partialMatchingImages.length}`
                );
                webDetection.partialMatchingImages.forEach(image => {
                    console.log(`  URL: ${image.url}`);
                    console.log(`  Score: ${image.score}`);
                });
            }

            if (webDetection.webEntities.length) {
                console.log(`Web entities found: ${webDetection.webEntities.length}`);
                webDetection.webEntities.forEach(webEntity => {
                    console.log(`  Description: ${webEntity.description}`);
                    console.log(`  Score: ${webEntity.score}`);
                });

                terms.push(webDetection.webEntities[0].description);
                response.brand = webDetection.webEntities[0].description;
            }

            if (webDetection.bestGuessLabels.length) {
                console.log(
                    `Best guess labels found: ${webDetection.bestGuessLabels.length}`
                );
                webDetection.bestGuessLabels.forEach(label => {
                    console.log(`  Label: ${label.label}`);
                });
            }
        })


    function getPrice() {
        // Return new promise 
        return new Promise(function (resolve, reject) {
            console.log("TERMS: ", terms);
            // Do async job
            request.post({
                "headers": { "content-type": "application/json" },
                "url": "https://us-central1-object-recognition-187803.cloudfunctions.net/function-1",
                "body": JSON.stringify({
                    "terms": terms
                })
            }, function (err, resp, body) {
                if (err) {
                    reject(err);
                } else {
                    resolve(JSON.parse(body));
                    console.log("Price: ", JSON.parse(body));
                }
            });
        });
    }

    Promise.all([labelPromise, logoPromise, webPromise]).then(values => {
        console.log(values);

        // Sending back to client
        // res.json(response);
    }).then(results => {
        getPrice().then(results => {
            response.price = results;
            res.json(response);
        });
    })
});

app.listen(port, () => console.log(`Express server is listening on port ${port}!`))