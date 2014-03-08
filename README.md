<p align="center">
  <img src="https://raw.github.com/julioz/CloudStickyNotes-android/master/img_stickynotes.png" width="250" />
</p>
Cloud Sticky Notes
========================

Cloud Sticky Notes rose from a necessity I had of syncing my "Windows-style" desktop Sticky Notes through the cloud. Windows 7 brought a nice "Sticky Notes" application that I use a lot, but unfortenately it is only local.
Since I was not always near my laptop to write reminders, I often forgot those bits of things to do in the everyday-life :)

There are some nice options out there to do this kind of job, but I thought that was too much 'overkill' to my needs. So, why not have some fun during a weekend or two and implement my own service? :)
Also, I had the opportunity to learn how to use the Dropbox API, which was something useful after all.

Alongside with this Android app, I wrote a simple Java-Swing application to run at my laptop... I'll probably release it after I finish some little things that still annoy me :)

So, wrapping up, the system works as follows: The user will login into his Dropbox account, and a folder with the name of the app will be created. There, all his notes will be stored in a simple JSON file, that can later be read by any other application in this environment (like the desktop app, or maybe an iOS or Windows Phone app...). Similarly, this app also reads that same JSON file to sync the notes, that way, everyone can talk to everyone.

I hope this is also useful to someone else out there... even if it isn't, it was fun writing it :)
If you have any suggestion to this project, I'll be happy to see your comments and of course, your pull request!
