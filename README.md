# Image to ASCII converter


## Description 
This is simple image to ASCII converter that i decided to make. 
There are some basic controls for files selection  and previewing resulting text, along some QoL.
You also can adjust sliders for gamma/contrast to achieve better visuals on the output. 
Aside from mentioned and obvious UI elements you can hold Ctrl while using mouse wheel 
to size up or down the font used for displaying the text. All of UI elements are tied with
java Swing UI so there shouldn't be issues with running it on different machines UI-wise.

## Limitations
~~One limitation is that in code was used `Math.Clamp`~~
That one is fixed so it should run on java versions 8 and above. The workflow for creating exe
still uses `java 21`, but it shouldn't matter as it doesnt compile anything and only reason to use this 
version is the fact that stable jpackage was added at `java 16` version

The only restriction is that app currently is only able to output images of lesser size than the original or equal 
because [box averaging](https://en.wikipedia.org/wiki/Box_blur) is method i used for downscaling,
and the only other way to resize/upscale would be interpolation. 
In practice upscaling isn't needed here anyway, since each character occupies real space, so even a 1:1 mapping would produce a large text output.
There are comments trying to show the way logic works (check in `ConversionUtils.java` for more info on method.) but if you unsure just use a link above.

## How to use
If results you are getting are not to your liking try to adjust the sliders for gamma/contrast or size of final output.
By default best result you can get is to prevent image stretching by using original image width with half of images height
(since monospace fonts usually have symbols with their height double of width).

## Other notes 
Theres also a simple github workflow built for this app to publish releases. If you want, check under the .github folder in source root. Since github has no inbuilt tools to 
specifically make releases, i used the ones from [softprops](https://github.com/softprops/action-gh-release) (thanks to them for having it in public). If you will be trying to 
use their imported actions too, make sure you have correct settings for your repository's actions (in *Settings* -> *Actions* -> *General* , the *Actions permissions* 
should have selected option for: under *Allow all actions and reusable workflows*). 

Since there is nothing special yet, the release has source code and two versions of app packaged for launch. 
Use jars to run with java, or .exe zip archive for windows so that you can run it out of box as-is. The .exe package needs all files extracted to be able to run.

__

In any case feel free to oversee and use this code to your liking as it is just a fun tool i made exactly for that.
