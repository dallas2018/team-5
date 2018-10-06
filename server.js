require('dotenv').load();
const express = require('express')
const app = express()
const port = process.env.PORT || 3000;
const admin = require('firebase-admin');

const serviceAccount = require(GOOGLE_SA);

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://jpmc4g-18.firebaseio.com",
    storageBucket: "jpmc4g-18.appspot.com"
});

const db = admin.firestore();
db.settings({ timestampsInSnapshots: true });

app.get('/', function (req, res) {
    res.send('Hello World!')
})

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
})

app.listen(port, () => console.log(`Express server is listening on port ${port}!`))