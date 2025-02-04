This app was developed as part of a team effort with the following contributors:
- Ayesha Aamer *aaaamer1*
- Francis Garcia *fgarcia06*
- Aditi Padhi *aditipadhii*
- Ayra Qutub *ayraqutub* (me)
- Akhil Sunil *llLucidll*


## Description:
We want a mobile application where people can sign up for events at community centres that are popular and fill up fast. We want to allow people with limitations such as work, disability, etc. to be able to sign up for these events fairly and not have to sit refreshing a webpage until they can get a chance at reserving a spot.

How? Lottery! If I am running swimming lessons for 20 kids, I will post my event or series of events and I will let everyone join the waiting list for a period of a week. After the week is up, I will ask the system to choose 20 kids to sign up. The system will then notify these kids (or their guardians), if they say no they don’t want swimming lessons, then the system will sample another child to sign up. I can monitor the progress and then get access to the final list of everyone who signs up. If perhaps someone cancels later I can cancel them in the app and a new applicant is drawn.

Lottery systems are great because you don’t have to first get a chance to go to an event, you just have to say you are interested and if you’re lucky you will be offered a chance. This gives people who need the time, the time to sign up properly without pressure. Accessibility!


## Features:
**Pooling System:**
- Organizers can draw from a waiting list of interested event attendees as selected participants.

**QR Code Scanning:**
- The app will generate a QR code when an event is created. This can be used on posters and promotional material.
- Entrants can scan QR promotional code to view details about the event and also join the waiting list

**Firebase Integration:**
- Utilizes Firebase for storing event and profile details, attendee lists, and real-time check-in status updates.

**Multi-User Interaction:**
- Distinguish between entrants, organizers, and admin with special roles and privileges granted to each actor.
- Users are identified by their device, using a unique device identifier, so that they don't have to use a username and password.

**Event Management:**
- Allows event organizers to create events and set details, upload and update event poster images, and to view and manage registrants.

**Profile Management:**
- Users can set and edit their profile information, like their name, email, phone number, etc.
- Users can upload a profile picture for a more personalized experience; if no image is uploaded, the profile picture will be deterministically generated from the profile name.

**Admin Responsibilities:**
- Users with an admin role are able to browse and remove events, profiles, facilities, and images that may violate app policy.

**Geolocation Verification:**
- Can use geolocation to verify where users are joining the waiting list from. This is the location provided by the device.
- Users are warned before they register for an event that requires geolocation.
- Organizers will be able to see these locations on a map for each of their created events.

**Notifications:**
- Event organizers can send notifications to entrants for their events who have been selected for or removed from an event.
- The entrants will then receive these as push notifications to their device. 
- Users can manage their notification preferences from their profile.


**Project Backlog: **
https://github.com/orgs/CMPUT301F24quartz/projects/1



**UI Mockups:**
![image](https://github.com/user-attachments/assets/75978685-9878-4a16-b9cb-aa6bce01100c)
