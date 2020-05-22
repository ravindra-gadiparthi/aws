> This Lambda is tend to execute when a image upload to S3 bucket, S3 publish Object put event to SNS topic

> Then SNS pushed the event to subscribed SQS event which is being polled by this Lambda.

> Lambda function detects labels and push data to MYSQL Database.