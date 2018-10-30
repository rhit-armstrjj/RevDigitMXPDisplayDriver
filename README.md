# RevDigitMXPDisplayDriver
A Driver for the Rev Robotics Digit MXP Display, found at: http://www.revrobotics.com/rev-11-1113/

# Documentation
The link to the docs can be found [here](https://lainkai.github.io/RevDigitMXPDisplayDriver/).

# How to use
This has dependencies. If you want to have a good time, I suggest you use gradle.

The artifacts can be found [here](https://bintray.com/bak3dnet/robotics/digital-display) at bintray.
Click on `Set Me Up!`. Click on the artifact you are using, be it Maven or Gradle.
Then copy the code, and paste into your build file. Then, copy the dependency snippet, and place it in the build file.

The main controlling class is `RevDigitDisplay`. It is the only display I have made drivers for, so far.
Use the `getInstance()` method, becuase its constructor is private. It's called a singleton. All info
can be found in the documentation.

Please notify me if you have anything you want me to add.
