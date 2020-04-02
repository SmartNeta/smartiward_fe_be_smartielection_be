# smartiward_fe_be_smartielection_be

Repository contains Smartiward UI and Smartiward Backend code.

Repository contains SmartiElection Backend code.


Deployment on server smartielection : 

connect putty.
cd to /home/sampark#
Run command lsof -i:8585 //process is running or not for 8585 port
kill -9 {p-id}  // if any process is running then kill process
git pull // for getting new commited changes 
mvn clean install
nohup java -jar target/{jar file}



Deployment on server smartiward : 

connect putty.
cd to /home/sampark_iward#
Run command lsof -i:8586 //process is running or not for 8586 port
kill -9 {p-id}  // if any process is running then kill process
git pull // for getting new commited changes 
mvn clean install
nohup java -jar target/{jar file}
