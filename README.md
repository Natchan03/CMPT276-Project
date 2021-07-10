# CMPT276 Project Proposal: YouNote

  YouNote is a web application allowing users to take and save notes on YouTube videos, where each note is associated with a timestamp in the video. Notes are written in a text input within a form, which also contains a “time” input for the timestamp, and are located besides an embedded video in the same window. This allows its users to seamlessly and effortlessly go back and forth between a video and their own notes to quickly gain refreshers on a section of a video.
	
  The notes can be saved by clicking a button to submit the form only if the user is logged into an account to associate the note with the account. Time-permitting, users may be able to submit notes without an account and be able to save them after signing up for an account in the same web session. In a page to view and maybe share saved notes, admin accounts can modify or delete saved notes from standard accounts, but standard accounts can also modify and delete their own saved notes if we have time to implement this.
	
  In order to use the web application, users will first create a standard account. They can then post a link to a YouTube video on a search box which may redirect users to a page containing an embedded video and text box (with timestamp input) beside it. The user can also write text on the search box to find and click on a video link. If the video cannot be embedded because it has been disabled by the creator, a link or thumbnail of the video will be provided in place of the video. If a link is submitted and turns out to be invalid, the user will be redirected back to the search page with an error message appearing to indicate this.
	
  The timestamp of the video is either manually written or automatically generated. If the video is embedded, writing on an empty note input with an empty timestamp will automatically record the timestamp of the video at that moment. If there was text on the note input and if the timestamp was automatically recorded, erasing all text will delete the timestamp.
	
  The IFrame API is our web API used to embed a YouTube video and get an automatic timestamp on the note-taking page. The YouTube Data API is our REST API used to search for a YouTube video from the search box.
	
  There are more optional features we may implement if we have time. Users can share specific notes with other users by giving them access to these notes. These notes are selected somehow and shared with another account by specifying their email. Users, particularly instructors, can create “rooms” and distribute videos from the playlist to all of the students at once using the YouTube Data API. Users can also favorite videos so that they can quickly access a set of videos.
	
  
  For what has already been implemented, Google Chrome extensions, such as “TubersLab - Youtube Notepad”, “Rocket Note”, and “YouNoteIt”, are applications with the ability to take notes while having the video open on YouTube. Although their goal is the same as our application, our web app instead displays the video and note input on our website instead of on YouTube. There are no web apps that can be easily found that implements our idea.
  
  Without these or similar applications, users - who do not wish to use Google Chrome or these extensions or simply do not know they exist - have to take their own notes on a separate text editor or on paper. If users wish to timestamp their notes, they will need to manually write it down. If users do not timestamp their notes and wish to know which video or section of the video it references, they will need to search to find that video or section of the video, which may take up a significant amount of the user’s time.
	
  Our target audience is any individual who wishes to gain knowledge from the vast library of publicly available videos on YouTube and want a note-taking software integrated with a YouTube video. This solves the problem of needing to search through libraries of watched content to find the video section referenced by a particular note they took. For example, a student studying remotely with access to recorded YouTube lecture videos would benefit from being able to watch lectures and take timestamped notes simultaneously on the same page.
	
  
  In our first iteration, we will implement a sign-up page for a standard user account, setting of the admin user accounts, and the login. Regular users, on a sign-up page, will be able to sign up for a standard account by typing in their email and desired password in text inputs, contained within a form element. Optionally, when the user submits this form, the server will send a verification email where the user must click on a link to activate their account, which is needed to login to their account and use its functionality. Another optional feature is to be able to sign up and login with a Google account instead of using an email.
	
  The Postgres database for our app will have a ‘User’ table storing the unique ID, email, hashed password, and type (standard or admin) of submitted user accounts from the sign-up page. Only authorized developers will be able to set the type of accounts to admin. Time-permitting, a feature is that admin accounts can delete user accounts from the User table.
	
  In a login page, a user will type in their input and hashed password to be sent to the server. If the information is correct, the server will set a cookie to create a session between the user’s browser and the server. After a set period of time, the session will expire, for the account’s security, and the user must login in again to access their account. Optionally, we can include a checkbox so that users can stay signed in without their sessions expiring.
  
 # Requirements & Specification Document: Iteration 1
  
  Project Abstract
  
  YouNote is a web application which allows users to take and save notes on YouTube videos, where each note is associated with a timestamp in the video. Notes are written in a text input within a form, which also contains a “time” input for the timestamp and are located besides an embedded video in the same window. The notes can be saved by clicking a button to submit the form only if the user is logged into an account to associate the note with the account. In a page to view and maybe share saved notes, admin accounts can modify or delete saved notes from standard accounts, but standard accounts can also modify and delete their own saved notes.

  Customer
  
  Our target audience is any individual who wishes to gain knowledge from the vast library of publicly available videos on YouTube and want a note-taking software integrated with a YouTube video. This solves the problem of needing to search through libraries of watched content to find the video section referenced by a particular note they took. For example, a student studying remotely with access to recorded YouTube lecture videos would benefit from being able to watch lectures and take time stamped notes simultaneously on the same page.
  
  Competitive Analysis
  
  For what has already been implemented, Google Chrome extensions, such as “TubersLab - Youtube Notepad'', “Rocket Note”, and “YouNoteIt”, are applications with the ability to take notes while having the video open on YouTube. Although their goal is the same as our application, our web app instead displays the video and note input on our website instead of on YouTube. There are no web apps that can be easily found that implements our idea. Moreover, YouNote will give users an option to save, edit and share the notes as well. Users will have to create an account and keep their notes saved securely in their account for future use.
  
  User Stories
  
  Signup and login for regular users
  *	Alex (User) is a university student who wishes to take notes while watching a YouTube video of his instructor using YouNote. As he is a new user to the application, he will first sign up to make an account in YouNote. By clicking on the “Signup” button, he will get text fields where he would have to enter his first name, last name, and email, as well as to create a password. After entering this information, he clicks the “Create my account” button to register his account.
  *	After creating an account, he can login into his account by entering his registered email and password in the text fields provided and then clicks the “Log In” button. Once logged in, it will show “Welcome Alex” indicating he has logged into his account. There will be a “Logout” button in the right-top dropdown menu which he can click to logout of his account.
  
  Login for admin user
  *	The admin account will automatically be created when If the admin wants to access his account in YouNote, he enters the email and password of the admin account in the login page and clicks the “Log In” button. Once the admin is logged in, the page will show “Welcome admin” to indicate that the admin has logged in. There will also be a “Logout” button which the admin can click to logout of his account.
  *	Feature for admin user to view all user accounts
  
  Testing our sample page and providing feedback
  * Alex (User) wants to know how the application works, test it, and provide feedback about it.
  *	He can click on the tutorial button on the homepage which will give him a brief description of the application and how to use it.
  *	He will have an option to test it as well. He can click on the test option which would be given beside the tutorial button on the home page. The test will show him the layout of the application. It will have a search box where he could enter the URL of the video for which he wants to take notes and then click on load video and then the embedded video will start playing. Along with a search box he will be provided a textbox where he could take notes while watching a video.
  *	It will only be a test page which he could use to see how the app works so it will not have an option to save the notes taken. 
  *	There will be an option on the homepage which will tell the user about the upcoming features of the application. He can click on upcoming features and have a look at all upcoming features of the application.
  *	He will also have an option of feedback. There will be a feedback button on the homepage. Once he clicks on that button, he will have an option to enter his first name, last name, and any suggestions he would like to give us. 

  Next Iterations
  
  *	After logging into the account, the user will get a search box where he could search the video, and a textbox where he could take the notes while watching the video. Notes will be associated with timestamp in the video.
  *	Users will have an option to save, edit or even share the notes.
  *	Regular users will have an option to delete their own notes if they wish to.
  *	Admin users will have an option to modify or delete notes from standard accounts.
  *	In the User's account, there will be my Videos page which will show the history of videos for which the user added notes.
  *	In the User's account, there will be a Shared with me page which will show videos that other users have shared.


