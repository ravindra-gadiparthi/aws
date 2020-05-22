var mysql = require('mysql');

var con = mysql.createConnection({
    host: "cloudcafe-photos-db.cw3u3wunzof3.ap-south-1.rds.amazonaws.com",
    user: "admin",
    password: "Testing1",
    database: "Photos"
});


exports.handler = async(event) => {

    if (event['Records'] && event['Records'].length > 1) {

        const s3PutEvent = event.records.s3;
        const bucketName = s3PutEvent.bucket.name;
        const object = s3PutEvent.object.key;
        console.log(`execution start file uploaded on ${bucketName} and object ${object} `);

        const promise = new Promise(function(resolve, reject) {

            con.connect(function(err) {
                if (err) reject(err);
                console.log("Connected!");

                const response = {
                    statusCode: 200,
                    body: JSON.stringify('Hello from Lambda!'),
                };

                con.query('select * from image', function(err, result) {
                    if (err) throw reject(err);
                    console.log("Result: " + result);
                    resolve(result);
                });

            });
        })
        return promise

    }
    console.log("execution end ");

};
