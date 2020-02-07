# Pong

This is a version of Pong I made using Java.
The goal was to make the gameplay as close to the original Atari gameplay as possible.

The paddles are controlled with 'w' and 's' for the left and up and down arrows for the right.
The space bar serves and escape can reset the round.

All of the mechanics are adjustable in the first section of the code labeled "define global gameplay parameters".
The settings I have are an attempt to match Atari footage.

The sound is a bit poor.
I wanted to keep everything self-contained and not use external samples, so the sounds are generated within the program.
The downside is the Java sound mechanics are quite slow, so the sound doesn't sync with the image very well.
This is why there is an option to disable the sound altogether.

There are a few specific references I used, most of which are mentioned within the code, but I will reiterate here:
Sound Generation
https://stackoverflow.com/questions/34611134/java-beep-sound-produce-sound-of-some-specific-frequencies

Retaining Focus
https://stackoverflow.com/questions/6723257/how-to-set-focus-on-jtextfield/

The original setup and fucntionality
http://www-classes.usc.edu/engr/ee-s/477p/s00/pong.html

Original Atari Footage
https://www.youtube.com/watch?v=fiShX2pTz9A
https://www.youtube.com/watch?v=e4VRgY3tkh0
