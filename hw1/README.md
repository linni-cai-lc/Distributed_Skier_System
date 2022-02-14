# HW1

SKI SERVLET
- BASE URL: http://localhost:8080/hw1_war_exploded/ski/
- OPERATIONS:
  - resorts
    - GET  http://localhost:8080/hw1_war_exploded/ski/resorts
    - GET  http://localhost:8080/hw1_war_exploded/ski/resorts/123/seasons/456/day/111/skiers
    - GET  http://localhost:8080/hw1_war_exploded/ski/resorts/123/seasons
    - POST http://localhost:8080/hw1_war_exploded/ski/resorts/123/seasons

  - skiers
    - POST http://localhost:8080/hw1_war_exploded/ski/skiers/123/seasons/456/days/111/skiers/3434
    - GET  http://localhost:8080/hw1_war_exploded/ski/skiers/123/seasons/456/days/111/skiers/3434
    - GET  http://localhost:8080/hw1_war_exploded/ski/skiers/3434/vertical
    - GET  http://localhost:8080/hw1_war_exploded/ski/statistics


- Upload war from local to AWS
```
sudo scp -i /Users/linni/Desktop/aws_0213.pem /Users/linni/Documents/CS6650/HW/hw1/out/artifacts/hw1_war/hw1_war.war ec2-user@54.200.234.195:/usr/share/tomcat/webapps
```

AWS example:
- http://54.200.234.195:8080/hw1_war/ski/resorts
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