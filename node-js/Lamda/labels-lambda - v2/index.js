var mysql = require('mysql');
const ImageAnalyser = require("ImageAnalyser")


var pool = mysql.createPool({
    connectionLimit: 10, // default = 10
    host: process.env.DB_HOST,
    user: process.env.DB_USERNAME,
    password: process.env.DB_PASSWORD,
    database: process.env.DATABASE
});

exports.handler = async(event) => {

    console.log(JSON.stringify(event));

    if (event['Records']) {

        const sqsBody = JSON.parse(event.Records[0].body)
        const sqsMessage = JSON.parse(sqsBody.Message);
        const s3PutEvent = sqsMessage.Records[0].s3;
        const bucketName = s3PutEvent.bucket.name;
        const key = s3PutEvent.object.key;
        const keys = key.split('/');

        const username = keys[0];
        const fileName = decodeURIComponent(keys[1].replace(/\+/g, " "));

        const s3Config = {
            bucket: bucketName,
            imageName: key,
        };

        console.log("key " + JSON.stringify(keys));
        console.log("bucketName " + bucketName);

        try {

            const labels = await ImageAnalyser.getImageLabels(s3Config);
            
            const imageLabels = labels.map(label => label.Name);
            
            console.log(imageLabels);
        
            var sql = `INSERT INTO image (name, bucket_name, username,labels) VALUES ( '${fileName}','${bucketName}','${username}','${imageLabels}')`;

            console.log("executing query " + sql);

           
           const results  = await new Promise(function(resolve, reject) {

                    pool.getConnection(function(err,con) {
                        if (err) return reject(err);
                        console.log("Connected!");
                        con.query(sql, function(err, result) {
                            try{
                            if (err) throw reject(err);
                            console.log("Result: " + result);
                            resolve(result);
                            }catch(e){
                                console.log(e);
                                reject(err);
                            }finally{
                                con.release();
                            }
                        });

                    });
                })
            
            return results;
                
        }
        catch (error) {
            console.log(error)
            return error;
        }


    }
    console.log("execution end ");

};
