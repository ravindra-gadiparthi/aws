var AWS = require('aws-sdk');


const s3 = new AWS.S3();

s3.listBuckets((error,data)=>{
    if(error){
        console.log(error);
    }
    
    console.log(data);
})

var bucketParams = {
  Bucket : 'awscloudcafe-ravidra',
};

s3.listObjects(bucketParams,(error,data) => {
    if(error){
        console.log(error);
    }else{
         console.log(data);
    }
})