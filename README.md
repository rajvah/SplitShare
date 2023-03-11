# SplitShare

 A 3-tier full stack application for management of expenses
 
 
This system has 3 major components, the User Interface, the Backend System and the Database. 

The User Interface: This component would provide access to the client to access the system. The client (User in terms of the application) would be able to send requests for user creation, user validation, creating a new split between friends, updating an existing split etc. The user would also be able to see the splits he/she is part of and the splits he/she owns on the user interface as well. The user interface is responsible for making the connection to the server, and all communication through the client will be made through HTTP calls. The user interface would be exposed to the following functionalities:
ConnectToServer(): This would be a behind the scenes call to connect the client application to the server for handling all future requests. The client will have a retry mechanism in case of failures. A connection failed message would be returned if the threshold for retries is exceeded. 
DisconnectWithServer(): A connection termination request would be sent from the client to the server when the client no longer wants to communicate. No error is returned in this case. 

The Backend System: This component would comprise a bunch of Application Program Interfaces (APIs) which would cater to the requests made by the client(s). The backend system ( also called the API layer ) is also responsible for fetching and inserting/updating data to and from the backend database system. The API layer would connect with the user interface and well as the database. 
The backend system will have the following functionalities:
 1) CreateUser(): Check if the user exists, if yes return an error message, if not return a success response for user creation. 
 2) UpdateUserDetails(): User is allowed to update his/her personal information on the system.
 3) DeleteUser(): A user can delete his account from the system. Upon success, they would no longer be able to access the system. 
 4) SpecifyFriends(): The user can specify the list of friends or acquaintances with whom they would need to share their bills regularly. The list is to be     the personal email address of the persons involved. If the user exists in the system, a success response is returned. If the user does not exist, an       error message would be returned notifying the issue. 
 5) GetAllFriends(): The user can fetch a list of all their friends that are part of the system, and can decide whether to split a particular bill with         them or not. 
 6) FetchOwnedSplits(): User can fetch the complete list of all the splits that they created. Each owned split can be edited and deleted individually.         Error message is returned in case the split does not exist or is not owned by them.
 7) GetSplitsAsMembers(): Get a list of all splits which have been shared with them. No edit or delete is allowed. 

The Database System: The database system will hold all the data related to the users, their friends, their splits and how much they owe. The system will also store information related to the splits as what is the amount to be split, who is the owner of the split, who will be the members that are a part of the split. There will be two tables in the system, a user table and a split table. The operations on both tables will be basic Create, Read, Update and Delete operations. However, for both tables there will be restrictions placed on update and delete operations based on what permissions the user has. For example, if the user does not own the split, they cannot update or delete the split or a user can only read, update or delete their own user. 
