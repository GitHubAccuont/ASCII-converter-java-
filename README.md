# Image to ASCII converter
---
This is simple image to ASCII converter that i decided to make. 
There are some basic controls for files selection  and previewing resulting text, along some QoL.
You also can adjust sliders for gamma/contrast to achieve better visuals on the output. 
Aside from mentioned and obvious UI elements you can hold Ctrl while using mouse wheel 
to size up or down the font used for displaying the text. All of UI elements are tied with
java Swing UI so there shouldnt be issues with running it on different machines.

Only limitation is that in code was used `Math.Clamp` which limits app usage to java 21+ versions. 

App currently is only able to output images of lesser size than the original or equal 
because [box averaging](https://en.wikipedia.org/wiki/Box_blur) is method i used for compression and it has some flaws,
including quality loss even if i adapted it to resizing instead. There are comments trying to show the way logic works but if you unsure just use a link above.

If results you are getting are not to your liking try to adjust the sliders for gamma/contrast or size of final output.
By default best result you can get without stretching image is using same width as of original and half of original's height
(Since monospace fonts usually have symbols with height double of width).

In any case feel free to oversee and use this code to your liking as it is just a tool i made exactly for that.