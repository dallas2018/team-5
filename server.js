require('dotenv').load();
const express = require('express')
const app = express()
const port = process.env.PORT || 3000;
const admin = require('firebase-admin');

var serviceAccount = require(process.env.GOOGLE_SA);

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://jpmc4g-18.firebaseio.com"
});

var db = admin.firestore();
db.settings({ timestampsInSnapshots: true });

db.collection('charity').get()
    .then((snapshot) => {
      snapshot.forEach((doc) => {
        console.log(doc.id, '=>', doc.data());
      });
    })
    .catch((err) => {
      console.log('Error getting documents', err);
    });

app.get('/', function (req, res) {
    res.send('Hello World!')
})

app.listen(port, () => console.log(`Express server is listening on port ${port}!`))