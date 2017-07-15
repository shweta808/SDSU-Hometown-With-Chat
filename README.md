# SDSU-Hometown-With-Chat

As the the number of user records on the server grows so does the time the app takes to load the data and the app 
keeps downloading the same data multiple times. Application has slow response times with only 2000 records.
What happens when there is 200,000 or 2,000,000 records.

To help the app scale we will do several things. 
First when you download data from the server put the data in an SQL database on the device. 
When it is time to display the data first check the local SQLite database for the data. 
This will be faster, particularly if you do the geocoding before you enter the data into the database. 
Second when you display a list of data fetch the data from the server in pages in reverse order so the user is 
getting most recent data first. Only fetch more data when the user scrolls down the list and the local database does 
not have the data. Third in the map view only display the most recent N records. Give the user the ability to fetch 
another N records. Of course the user can do this multiple times.

Chat:
Firebase is used to store real time chat of users.

