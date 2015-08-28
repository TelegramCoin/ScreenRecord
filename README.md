# Screen Recorder for Testing

Screen Record library is a testing tool for Android applications. It basically creates an overlay inside your application
to record your actions, view interactions and navigation while using the app.

## Usage

Integrating this library to any application is very straight-forward.

###1) Get the library

Either clone this repository or download it. After getting the repository, copy the library folder to your project on top level.
It means that the library should be at the same level with 'app' module.

Then add these lines to corresponding folders:

__settings.gradle__
```
include ':library'
```

__build.gradle__
```
compile project(':library')
```

###2) Initialization

Now you can use the library with your project. It is time to initialize the needed service and let it do its work. To do that,
you have to extend the Application class of your project. [Here](http://www.devahead.com/blog/2011/06/extending-the-android-application-class-and-dealing-with-singleton/) you can find a good tutorial for doing this.

After you have your new application class, add this line in onCreate method.

```
ScreenRecord.
            with(this).
            place(Place.BOTTOM_LEFT).
            size(Size.MEDIUM).
            xposedInjection(true).
            start();
```

ScreenRecord class has many methods to customize this tool for your needs.

### Developer Notes

This project was designed and implemented during my internship at Monitise MEA. I will be continuing to develop and maintain this project.
Also, this README is not sufficient. I will be working on this problem and expand the README as a guideline.
