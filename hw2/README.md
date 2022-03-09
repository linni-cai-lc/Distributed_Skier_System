# HW2

SKI SERVLET
- BASE URL: http://localhost:8080/hw2_war_exploded/ski/
- OPERATIONS:
  - resorts
    - GET  http://localhost:8080/hw2_war_exploded/ski/resorts
    - GET  http://localhost:8080/hw2_war_exploded/ski/resorts/123/seasons/456/day/111/skiers
    - GET  http://localhost:8080/hw2_war_exploded/ski/resorts/123/seasons
    - POST http://localhost:8080/hw2_war_exploded/ski/resorts/123/seasons

  - skiers
    - POST http://localhost:8080/hw2_war_exploded/ski/skiers/123/seasons/456/days/111/skiers/3434
    - GET  http://localhost:8080/hw2_war_exploded/ski/skiers/123/seasons/456/days/111/skiers/3434
    - GET  http://localhost:8080/hw2_war_exploded/ski/skiers/3434/vertical
    - GET  http://localhost:8080/hw2_war_exploded/ski/statistics


- Upload server war from local to AWS
```
sudo scp -i /Users/linni/Desktop/aws_0213.pem /Users/linni/Documents/CS6650/HW/hw2/server/out/artifacts/server_war/server_war.war ec2-user@54.200.234.195:/usr/share/tomcat/webapps
```
- Upload consumer jar from local to AWS
```
sudo scp -i /Users/linni/Desktop/aws_0213.pem /Users/linni/Documents/CS6650/HW/hw2/consumer/out/artifacts/consumer_jar/consumer_64.jar ec2-user@18.236.237.22:
```

AWS example:
- GET http://54.200.234.195:8080/hw2_war/ski/resorts
  ```
  {
    "resorts": [
      {
        "resortName": "Creek Lake",
        "resortID": 0
      }
    ]
  }
  ```
- POST http://54.200.234.195:8080/server_war/ski/skiers/12/seasons/13/days/14/skiers/16
  - Request Body JSON
  ```
  {
    "time": 217,
    "liftID": 21,
    "waitTime": 3
  }
  ```
  - Return `Write successful`