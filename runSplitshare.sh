# UI
cd SplitShareUI
ng serve &

#Server

cd ../SplitShare
mvn clean install
java -jar  target/splitshare-1.0-SNAPSHOT.jar server config.yaml