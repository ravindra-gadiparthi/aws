1)Upload the application to Cloud 9

build using mvn:spring-boot:run (considering you have done java & maven set up already)



2)Go to the AWS X-Ray daemon documentation link below:
https://docs.aws.amazon.com/xray/latest/devguide/xray-daemon.html
On the documentation page, scroll down until you see a link to Linux (executable)-aws-xray-daemon-linux-2.x.zip (sig). Right-click the link and copy the link address.
In your AWS Cloud9 instance terminal, type the command below to go to your home directory.
cd ~

Type wget and paste the AWS X-Ray daemon hyperlink address that you copied. The command should look like the example below.
wget https://s3.dualstack.us-east-2.amazonaws.com/aws-xray-assets.us-east-2/xray-daemon/aws-xray-daemon-linux-2.x.zip

Unzip the AWS X-Ray daemon by typing the command below. Make sure that the name of the .zip file matches the one in the command below.
unzip aws-xray-daemon-linux-2.x.zip

Run the AWS X-Ray daemon by typing the command below.
./xray


3) Hit the application it should send segments to xray


