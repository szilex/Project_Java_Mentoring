Repository contains Spring Boot project titled "Mentoring". It's purpose is to allow for a single mentor to create and post meetings and for students to make reservations for the meetings. 

The application is available at: 

http://euvicmentoringapplication.eba-kpwxpiq6.eu-central-1.elasticbeanstalk.com/

The available endpoints are:

-     /meeting/{id} (GET) - allows to get informations about specified Meeting, can be accessed only by authenticated users
-     /meeting (GET) - allows to get informations about all Meetings, can be accessed only by authenticated users
-     /meeting (POST) - used to create new Meetings, can be accessed only by Mentor
-     /meeting (PUT) - used to update Meeting by inserting student id, can be accessed only by Student
-     /meeting/{id} (DELETE) - allows to delete specified Meeting, can be accessed only by Mentor
-     /user/mentor (GET) - allows to get informations about Mentor, can be accessed only by Mentor
-     /user/student/{id} (GET) - allows to get informations about specified Student, can be accessed only by Mentor
-     /user/student (GET) - allows to get informations about all Students for Mentor or only own informations for Student
-     /user/student (POST) - used to insert new Student, can be accessed only by unauthorized user
-     /user/student (PUT) - used to change credentials of Student, can be accessed only by Student
-     /user/student/{id} (DELETE) - allows to delete specified Student, can be accessed only by Student