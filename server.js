require('dotenv').load();
const express = require('express')
const app = express()
const port = process.env.PORT || 3000;
const admin = require('firebase-admin');
const vision = require('@google-cloud/vision');

const serviceAccount = require('./jpmc4g-18-2c6ced10667d.json');

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
})

const Multer = require('multer');
const bucket = admin.storage().bucket();

const multer = Multer({
    storage: Multer.memoryStorage(),
    limits: {
        fileSize: 5 * 1024 * 1024
    }
});

// Adding new file to the firebase storage
app.post('/upload', multer.single('file'), function (req, res) {
    console.log('Upload Image');

    const filename = req.file.originalname;
    const fileupload = bucket.file(filename);
    const uploadStream = fileupload.createWriteStream({
        metadata: {
            contentType: file.mimetype
        }
    });
    uploadStream.on('error', (err) => {
        console.log(err);
        return;
    });

    uploadStream.on('finish', () => {
        console.log('Upload success');
    });

    uploadStream.end(file.buffer);
    // const blob = bucket.file(req.file.originalname);
    // const blobStream = blob.createWriteStream({
    //     metadata: {
    //         contentType: req.file.mimetype
    //     }
    // });

    // blobStream.on("error", err => {
    // });
    // blobStream.on("finish", () => {
    // });
    
    // let filename = req.file.originalname;
    // console.log(req.protocol + req.file.path);
    // console.log(filename);

    // bucket.upload(filename).then(() => {
    //     console.log(`File ${filename} uploaded to gs`);
    // })
    // .catch(err => {
    //     console.error('ERROR:', err);
    // });

    // if (filename) {
    //     bucket.upload(filename, {
    //         gzip: true,
    //         metadata: {
    //             cacheControl: 'public, max-age=31536000',
    //         },
    //     })
    //     .then(() => {
    //         console.log(`${filename} uploaded to bucket.`);
    //     })
    //     .catch(err => {
    //         console.error('ERROR:', err);
    //     });
    // }
});

app.get('/label', function (req, res) {

    var response = new Object();

    const bucketName = 'jpmc4g-18.appspot.com';
    const fileName = 'test/swell.jpg';
    const client = new vision.ImageAnnotatorClient();
    
    const labelPromise = client
        .labelDetection(`gs://${bucketName}/${fileName}`)
        .then(results => {
            const labels = results[0].labelAnnotations;
            console.log('Labels:');
            labels.forEach(label => console.log(label.description));

            response.label = labels[0].description;
        })
        .catch(err => {
            console.error('ERROR:', err);
        });

    const logoPromise = client
        .logoDetection(`gs://${bucketName}/${fileName}`)
        .then(results => {
            const logos = results[0].logoAnnotations;
            console.log('Logos:');
            logos.forEach(logo => console.log(logo));

            response.brand = logos[0]
        })
        .catch(err => {
            console.error('ERROR:', err);
        });

    const webPromise = client
        .webDetection(`gs://${bucketName}/${fileName}`)
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

                response.webEntity = webDetection.webEntities[0].description;
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

    Promise.all([labelPromise, logoPromise, webPromise]).then(values => {
        console.log(values);
        res.json(response);
    })
})



app.listen(port, () => console.log(`Express server is listening on port ${port}!`))