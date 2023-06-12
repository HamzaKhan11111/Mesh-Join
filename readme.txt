****************** Configuring MySQL Server ***********************
1) Choose a connection and change Hostname to 'localhost' ,username to 'root' and port should be 3306

****************** CREATING DATA WAREHOUSE ***********************
1)Open createDW
2)Run the whole script to create the database and tables
(It is assumed that Transactional _ MasterData Generator.sql Script has already been executed and its data is present)

****************** Executing Mesh Join Algorithm ***********************
1) place meshJoin folder in eclipse workspace
2) Open Eclispe
3) Go to File > Open File...
4) Select the file in the given path:  eclipse-workspace\meshJoin\src\meshJoin.java
5) execute the file by Run as -> 1 MainClass 
6) you will be prompted in terminal to write down the passowrd to connection. Write that
7) mesh join will be executed and data will be sent to data warehouse.


****************** Executing Queries ***********************
1) Open queriesDW.sql
2) Execute each query one by one to see the output (Question number and explanation is commented)